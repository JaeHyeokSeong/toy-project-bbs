$(document).ready(function () {

    $("#go-list").click(function () {
        location.href = '/bbs';
    });

    $("#delete-btn").click(function () {
        return confirm("게시물을 정말 삭제하겠습니까?");
    });

    $("#thumbs-up").click(function () {
        //반대쪽 반응 먼저 없애기
        const thumbs_down = $("#thumbs-down-class");
        thumbs_down.removeClass('bi bi-hand-thumbs-down-fill');
        thumbs_down.addClass('bi bi-hand-thumbs-down');

        //thumbs-up-class 시작
        const thumbs_up = $("#thumbs-up-class");
        const hasClass = thumbs_up.hasClass('bi bi-hand-thumbs-up-fill');
        if (hasClass) {
            thumbs_up.removeClass('bi bi-hand-thumbs-up-fill');
            thumbs_up.addClass('bi bi-hand-thumbs-up');
        } else {
            thumbs_up.removeClass('bi bi-hand-thumbs-up');
            thumbs_up.addClass('bi bi-hand-thumbs-up-fill');
        }

        // boardId 가져오기
        const boardId = $("#board-id").text().trim();

        // 서버에 POST 요청
        $.ajax({
            url: `/api/board-reaction/${boardId}`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({reactionType: 'LIKE'}),
            success: function (response) {
                $("#total-reaction-ct").text(response.totalReactionCounts);
            },
            error: function (xhr, status, error) {
                alert('좋아요 응답이 실패했습니다. 나중에 다시 시도해 주세요.');
            }
        });
    });

    $("#thumbs-down").click(function () {
        //반대쪽 반응 먼저 없애기
        const thumbs_up = $("#thumbs-up-class");
        thumbs_up.removeClass('bi bi-hand-thumbs-up-fill');
        thumbs_up.addClass('bi bi-hand-thumbs-up');

        //thumbs-down-class 시작
        const thumbs_down = $("#thumbs-down-class");
        const hasClass = thumbs_down.hasClass('bi bi-hand-thumbs-down-fill');
        if (hasClass) {
            thumbs_down.removeClass('bi bi-hand-thumbs-down-fill');
            thumbs_down.addClass('bi bi-hand-thumbs-down');
        } else {
            thumbs_down.removeClass('bi bi-hand-thumbs-down');
            thumbs_down.addClass('bi bi-hand-thumbs-down-fill');
        }

        // boardId 가져오기
        const boardId = $("#board-id").text().trim();

        // 서버에 POST 요청
        $.ajax({
            url: `/api/board-reaction/${boardId}`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({reactionType: 'DISLIKE'}),
            success: function (response) {
                $("#total-reaction-ct").text(response.totalReactionCounts);
            },
            error: function (xhr, status, error) {
                alert('좋아요 응답이 실패했습니다. 나중에 다시 시도해 주세요.');
            }
        });
    });
});

//답변
$(document).ready(function () {
    const boardId = $("#board-id").text().trim();
    let page = 0, size = 10, loading = false, isLast = false;

    //답변 불러오기 (페이징 + 무한 스크롤)
    function loadComments() {
        if (isLast || loading) return;
        loading = true;
        $("#loading-spinner").show();

        // select 박스에서 현재 선택된 값 가져오기
        const searchSort = $("#search-sort").val();

        $.ajax({
            url: `/api/comments/${boardId}`,
            method: 'GET',
            data: {
                page,
                size,
                parentCommentId: null,
                searchSort: searchSort
            }
        })
            .done(data => {

                $("#comment-count").text(`답변 ${data.totalElements}개`);
                isLast = data.last;

                if (page === 0 && data.content.length === 0) {
                    $("#comments-container").html(`
                <div class="no-comments">
                    답변을 기다리고 있는 게시물이에요<br>
                    첫번째 답변을 남겨보세요!
                </div>`);
                    return;
                }

                data.content.forEach(c => {
                    const safeContent = $('<div>').text(c.content).html();
                    const totalReaction = Number(c.totalLikes) - Number(c.totalDislikes);

                    // ← 여기를 추가!
                    const upIconClass = c.reactionType === 'LIKE'
                        ? 'bi bi-hand-thumbs-up-fill'
                        : 'bi bi-hand-thumbs-up';
                    const downIconClass = c.reactionType === 'DISLIKE'
                        ? 'bi bi-hand-thumbs-down-fill'
                        : 'bi bi-hand-thumbs-down';

                    const html = `
                <div class="comment-item" data-id="${c.commentId}">
                
                    <div class="comment-action-buttons d-flex flex-column align-items-center">
                        <button class="btn" id="comment-thumbs-up">
                            <i id="thumbs-up-class"  class="${upIconClass}"></i>
                        </button>
                        <p id="total-reaction-ct">${totalReaction}</p>
                        <button class="btn" id="comment-thumbs-down">
                            <i id="thumbs-down-class" class="${downIconClass}"></i>
                        </button>
                    </div>
                
                    <div class="comment-body">
                        <div class="comment-meta-row">
                            <div class="comment-avatar">
                                <img
                                    src="${c.profileImageUrl || '/images/default-profile.svg'}"
                                    alt="${c.writerName} 프로필 사진"
                                    class="avatar-img"
                                />
                            </div>
                            <div class="comment-meta-info">
                            
                            <!-- ② 여기에 comment-header 살려두기 -->
                            <div class="comment-header">
                            <span class="comment-author">${c.writerName}</span>
                            
                            ${c.isEditable ? `
                            <div class="comment-menu">
                                <button class="menu-toggle"><i class="bi bi-three-dots-vertical"></i></button>
                                <div class="comment-actions-owner">
                                   <button class="edit-btn"   data-id="${c.commentId}">
                                     <i class="bi bi-pencil"></i> 수정
                                   </button>
                                   <button class="delete-btn" data-id="${c.commentId}">
                                     <i class="bi bi-trash3"></i> 삭제
                                   </button>
                                </div>
                            </div>` : ''}
                            
                            </div>
                            
                            <!-- ③ 날짜는 header 바로 옆으로 -->
                            <span class="comment-date">
                                ${formatDate(new Date(c.createdDate))}
                                ${c.createdDate !== c.lastModifiedDate ? ' (수정됨)' : ''}
                            </span>
                            </div>
                        </div>
                        
                        <div class="comment-content mt-3" data-original="${safeContent}">${safeContent}</div>
                        
                        <button class="reply-comment-child-btn">답글</button>
                    </div>
                </div>`;
                    $("#comments-container").append(html);
                });

                page++;
            })
            .fail(err => console.error("답변 로드 실패", err))
            .always(() => {
                $("#loading-spinner").hide();
                loading = false;
            });
    }

    //답변 등록용 textarea 이벤트
    $('#add-comment-parent').on('focus click input', function () {
        $('#add-comment-parent').attr('rows', 10);
        $('#submit-comment-parent, #cancel-comment-parent').show();
    });

    //등록 취소
    $('#cancel-comment-parent').on('click', function () {
        $('#add-comment-parent')
            .val('')
            .attr('rows', 1)
            .blur();
        $('#submit-comment-parent, #cancel-comment-parent').hide();
    });

    //답변 등록
    $('#submit-comment-parent').on('click', function () {
        const raw = $('#add-comment-parent').val();
        if (!raw.trim()) return alert('답변 내용을 입력해주세요.');

        $.ajax({
            url: `/api/comment/${boardId}`,
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({content: raw, parentCommentId: null}),

            beforeSend: function () {
                // 스피너 보이기, 버튼 숨기기
                $('#submit-spinner').show();
                $('#submit-comment-parent, #cancel-comment-parent').hide();
            }
        })
            .done(() => {
                page = 0;
                isLast = false;
                $('#comments-container').empty();
                loadComments();
                $('#add-comment-parent')
                    .val('')
                    .attr('rows', 1)
                    .css({
                        height: 'auto',
                        overflowY: 'hidden'
                    })
                    .blur();
            })
            .fail(err => {
                console.error('댓글 등록 실패', err);
                alert('댓글 등록에 실패했습니다.');
            })
            .always(function () {
                // 요청 완료 후 스피너 숨기고 버튼 복귀
                $('#submit-spinner').hide();
                $('#submit-comment-parent, #cancel-comment-parent').hide();
            });
    });

    //수정 토글 및 에디트 모드 진입/취소
    $(document).on('click', '.edit-btn', function () {
        const id = $(this).data('id');
        const $item = $(`.comment-item[data-id="${id}"]`);
        const $cnt = $item.find('.comment-content');

        // 이미 에디트 모드면 취소
        if ($item.find('.edit-textarea').length) {
            $item.find('.edit-textarea, .edit-controls').remove();
            $cnt.show();
            return;
        }

        // 에디트 모드
        const orig = $cnt.data('original');
        const $ta = $(`<textarea class="edit-textarea mt-3" rows="10"></textarea>`)
            .val(orig)
            .insertAfter($cnt);
        $cnt.hide();
        $(`<div class="edit-controls">
                <button class="save-edit-btn" data-id="${id}">저장</button>
                <button class="cancel-edit-btn" data-id="${id}">취소</button>
                <div class="inline-spinner spinner" style="display:none; width:16px; height:16px; margin-left:4px;"></div>
          </div>`).insertAfter($ta);
    });

    //에디트 취소 버튼
    $(document).on('click', '.cancel-edit-btn', function () {
        const id = $(this).data('id');
        const $item = $(`.comment-item[data-id="${id}"]`);
        $item.find('.edit-textarea, .edit-controls').remove();
        $item.find('.comment-content').show();
    });

    //에디트 저장 → PUT
    $(document).on('click', '.save-edit-btn', function () {
        const id = $(this).data('id');
        const $item = $(`.comment-item[data-id="${id}"]`);
        const raw = $item.find('.edit-textarea').val();
        if (!raw.trim()) return alert('내용을 입력해주세요.');

        const $controls = $item.find('.edit-controls');
        const $save = $controls.find('.save-edit-btn');
        const $cancel = $controls.find('.cancel-edit-btn');
        const $spin = $controls.find('.inline-spinner');

        $.ajax({
            url: `/api/comment/${id}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({commentId: id, content: raw}),
            beforeSend: function () {
                $save.hide();
                $cancel.hide();
                $spin.show();
            }
        })
            .done(() => {
                page = 0;
                isLast = false;
                $('#comments-container').empty();
                loadComments();
            })
            .fail(err => {
                console.error('수정 실패', err);
                alert('댓글 수정에 실패했습니다.');
            })
            .always(() => {
                $spin.hide();
                $save.show();
                $cancel.show();
            });
    });

    //삭제 → DELETE
    $(document).on('click', '.delete-btn', function () {
        const $btn = $(this);
        const id = $(this).data('id');
        const $actions = $btn.closest('.comment-actions-owner');
        const $editBtn = $actions.find('.edit-btn');
        const $deleteBtn = $actions.find('.delete-btn');
        const $spin = $actions.find('.inline-spinner');

        if (!confirm('답변을 정말 삭제하시겠습니까?')) return;

        $.ajax({
            url: `/api/comment/${id}`,
            method: 'DELETE',
            beforeSend: function () {
                $editBtn.hide();
                $deleteBtn.hide();
                $spin.show();
            }
        })
            .done(() => {
                page = 0;
                isLast = false;
                $('#comments-container').empty();
                loadComments();
            })
            .fail(err => {
                console.error('삭제 실패', err);
                alert('댓글 삭제에 실패했습니다.');
            })
            .always(() => {
                $spin.hide();
                $editBtn.show();
                $deleteBtn.show();
            });
    });

    //페이지 전체 스크롤 → 추가 로드
    $(window).on('scroll', function () {
        if ($(window).scrollTop() + $(window).height() >= $(document).height() - 100) {
            loadComments();
        }
    });

    //정렬 조건이 바뀌는 경우
    $('#search-sort').on('change', function () {
        // 페이지 초기화
        page = 0;
        isLast = false;
        // 기존 댓글 지우기
        $('#comments-container').empty();
        loadComments();
    });

    //초기 로드
    loadComments();
});

function formatDate(date) {
    const Y = date.getFullYear();
    const M = String(date.getMonth() + 1).padStart(2, '0');
    const D = String(date.getDate()).padStart(2, '0');
    const h = String(date.getHours()).padStart(2, '0');
    const m = String(date.getMinutes()).padStart(2, '0');
    const s = String(date.getSeconds()).padStart(2, '0');
    return `${Y}.${M}.${D} ${h}:${m}:${s}`;
}

$(document).on('click', '.menu-toggle', function(e) {
    e.stopPropagation();  // 외부 클릭 이벤트 막기
    const $menu = $(this).closest('.comment-menu');
    $('.comment-menu.open').not($menu).removeClass('open');
    $menu.toggleClass('open');
});

// 빈 공간 클릭 시 닫기
$(document).on('click', function() {
    $('.comment-menu.open').removeClass('open');
});

$(document).ready(function () {
    let isLoggedIn = false;

    // 1) 페이지 로드 시 로그인 여부 확인
    $.getJSON('/api/member/profile')
        .done(res => {
            // data가 null 이면 비로그인, 아닐 때만 로그인
            isLoggedIn = !!res.data;
        })
        .fail(() => {
            // 네트워크 오류라도 일단 비로그인으로 처리
            isLoggedIn = false;
        });

    // 2) 댓글 리액션 클릭 핸들러
    $(document).on('click', '#comment-thumbs-up, #comment-thumbs-down', function(e) {
        e.preventDefault();

        // 비로그인 시 바로 경고
        if (!isLoggedIn) {
            return alert('로그인이 필요합니다.');
        }

        const isUp       = $(this).is('#comment-thumbs-up');
        const commentItem= $(this).closest('.comment-item');
        const commentId  = commentItem.data('id');
        const upIcon     = commentItem.find('#thumbs-up-class');
        const downIcon   = commentItem.find('#thumbs-down-class');
        const wasUp      = upIcon.hasClass('bi-hand-thumbs-up-fill');
        const wasDown    = downIcon.hasClass('bi-hand-thumbs-down-fill');
        const reactionType = isUp
            ? 'LIKE'
            : 'DISLIKE';

        // 3) 리액션 요청 (401 → 로그인 필요)
        $.ajax({
            url: `/api/comment-reaction/${commentId}`,
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ reactionType }),
            dataType: 'json'
        })
            .done(resultDto => {
                // 카운트 업데이트
                commentItem.find('#total-reaction-ct')
                    .text(resultDto.totalReactionCounts);

                // 아이콘 토글
                if (isUp) {
                    if (wasUp) {
                        upIcon.removeClass('bi-hand-thumbs-up-fill').addClass('bi-hand-thumbs-up');
                    } else {
                        upIcon.removeClass('bi-hand-thumbs-up').addClass('bi-hand-thumbs-up-fill');
                        downIcon.removeClass('bi-hand-thumbs-down-fill').addClass('bi-hand-thumbs-down');
                    }
                } else {
                    if (wasDown) {
                        downIcon.removeClass('bi-hand-thumbs-down-fill').addClass('bi-hand-thumbs-down');
                    } else {
                        downIcon.removeClass('bi-hand-thumbs-down').addClass('bi-hand-thumbs-down-fill');
                        upIcon.removeClass('bi-hand-thumbs-up-fill').addClass('bi-hand-thumbs-up');
                    }
                }
            })
            .fail((xhr) => {
                // 401 Unauthorized → 로그인 필요
                if (xhr.status === 401) {
                    alert('로그인이 필요합니다.');
                } else {
                    console.error('리액션 적용 실패', xhr);
                    alert('리액션 요청 중 오류가 발생했습니다.');
                }
            });
    });
});