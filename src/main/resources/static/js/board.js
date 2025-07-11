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
                $("#total-reaction-ct").text(response.data.totalReactionCounts);
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
                $("#total-reaction-ct").text(response.data.totalReactionCounts);
            },
            error: function (xhr, status, error) {
                alert('좋아요 응답이 실패했습니다. 나중에 다시 시도해 주세요.');
            }
        });
    });
});

//답변
$(document).ready(function () {

    let isLoggedIn = false;
    let userName = '';

    // 1) 페이지 로드 시 로그인 여부 확인
    $.getJSON('/api/member/profile')
        .done(res => {
            // data가 null 이면 비로그인, 아닐 때만 로그인
            isLoggedIn = !!res.data;

            if (isLoggedIn) {
                userName = res.data.name;
            }
        })
        .fail(() => {
            // 네트워크 오류라도 일단 비로그인으로 처리
            isLoggedIn = false;
        });

    $(document).on('click', '.comment-thumbs-up, .comment-thumbs-down', function(e) {
        e.preventDefault();
        if (!isLoggedIn) return alert('로그인이 필요합니다.');

        const $btn      = $(this);
        const isUp      = $btn.hasClass('comment-thumbs-up');
        const $action   = $btn.closest('.comment-action-buttons');
        const $item     = $action.closest('.comment-item');
        const commentId = $item.data('id');

        // 이 버튼 바로 옆의 카운트, 그리고 반대 아이콘
        const $count      = $action.find('.reaction-ct');
        const $thisIcon   = $btn.find('i');
        const $otherIcon  = isUp
            ? $action.find('.comment-thumbs-down i')
            : $action.find('.comment-thumbs-up i');

        $.ajax({
            url: `/api/comment-reaction/${commentId}`,
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ reactionType: isUp ? 'LIKE' : 'DISLIKE' }),
            dataType: 'json'
        })
            .done(dto => {
                // 1) 이 댓글의 카운트만 갱신
                $count.text(dto.data.totalReactionCounts);

                // 2) 이 버튼만 토글, 반대 버튼은 리셋
                if (isUp) {
                    if ($thisIcon.hasClass('bi-hand-thumbs-up-fill')) {
                        $thisIcon.toggleClass('bi-hand-thumbs-up-fill bi-hand-thumbs-up');
                    } else {
                        $thisIcon
                            .removeClass('bi-hand-thumbs-up')
                            .addClass('bi-hand-thumbs-up-fill');
                        $otherIcon
                            .removeClass('bi-hand-thumbs-down-fill')
                            .addClass('bi-hand-thumbs-down');
                    }
                } else {
                    if ($thisIcon.hasClass('bi-hand-thumbs-down-fill')) {
                        $thisIcon.toggleClass('bi-hand-thumbs-down-fill bi-hand-thumbs-down');
                    } else {
                        $thisIcon
                            .removeClass('bi-hand-thumbs-down')
                            .addClass('bi-hand-thumbs-down-fill');
                        $otherIcon
                            .removeClass('bi-hand-thumbs-up-fill')
                            .addClass('bi-hand-thumbs-up');
                    }
                }
            })
            .fail(xhr => {
                if (xhr.status === 401) alert('로그인이 필요합니다.');
                else alert('오류가 발생했습니다.');
            });
    });

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

                $("#comment-count").text(`답변 ${data.data.totalCount}개`);
                isLast = data.data.last;

                if (page === 0 && data.data.totalCount === 0) {
                    $("#comments-container").html(`
                    <div class="no-comments">
                        답변을 기다리고 있는 게시물이에요<br>
                        첫번째 답변을 남겨보세요!
                    </div>`);
                    return;
                }

                data.data.items.forEach(c => {
                    const safeContent = $('<div>').text(c.content).html();

                    const upIconClass = c.reactionType === 'LIKE'
                        ? 'bi bi-hand-thumbs-up-fill'
                        : 'bi bi-hand-thumbs-up';
                    const downIconClass = c.reactionType === 'DISLIKE'
                        ? 'bi bi-hand-thumbs-down-fill'
                        : 'bi bi-hand-thumbs-down';

                    // 대댓글 버튼 HTML 추가
                    let replyButton = '';
                    if (c.totalChildComments > 0) {
                        replyButton = `
                            <button
                                class="btn btn-link text-decoration-none d-flex align-items-center mt-1 load-child-comments"
                                data-id="${c.commentId}"
                                data-page="0"
                                data-total="${c.totalChildComments}"
                                style="font-size: 16px"
                            >
                                <i class="bi bi-chevron-down ms-1"></i>&nbsp&nbsp답글 ${c.totalChildComments}개
                            </button>`;
                    }

                    const html = `
                    <div class="comment-item" data-id="${c.commentId}">
                    
                        <div class="comment-action-buttons d-flex flex-column align-items-center">
                            <button class="btn comment-thumbs-up">
                                <i class="${upIconClass}"></i>
                            </button>
                            <p class="reaction-ct">${c.totalLikesPlusTotalDislikes}</p>
                            <button class="btn comment-thumbs-down">
                                <i class="${downIconClass}"></i>
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
                                    ${c.createdDate !== c.lastModifiedDate ? '*' : ''}
                                </span>
                                </div>
                            </div>
                            
                            <div class="comment-content mt-3" data-original="${safeContent}">${safeContent}</div>
                            
                            <!-- 변경된 답글 영역 -->
                            <div class="d-flex flex-column align-items-start">
                                <!-- 항상 노출되는 '답글' 버튼 -->
                                <button class="btn btn-link text-decoration-none d-flex align-items-center reply-comment-child-btn">
                                    답글
                                </button>
                                <!-- '답글 N개' 버튼 (아이콘 포함) -->
                                ${replyButton}
                            </div>
                            
                            <div class="child-comments-container"></div>
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

    const COMMENT_PAGE_SIZE = 10;

    // ── 3) 대댓글 로드 함수 추가 ──
    function loadChildComments($replyBtn) {
        const boardId    = $("#board-id").text().trim();
        const commentId  = $replyBtn.data('id');
        let   childPage  = $replyBtn.data('page');
        let isLast = true;
        const searchSort = $("#search-sort").val();
        const $cont = $replyBtn.closest('.comment-body').find('.child-comments-container');

        $.ajax({
            url: `/api/comments/${boardId}`,
            method: 'GET',
            data: {
                page: childPage,
                size: COMMENT_PAGE_SIZE,
                parentCommentId: commentId,
                searchSort: searchSort
            }
        })
            .done(data => {
                $replyBtn.html(`<i class="bi bi-chevron-up ms-1"></i>&nbsp&nbsp답글 ${data.data.totalCount}개`);
                isLast = data.data.last;
                data.data.items.forEach(c => {
                    const safe = $('<div>').text(c.content).html();
                    const upCls = c.reactionType === 'LIKE' ? 'bi-hand-thumbs-up-fill' : 'bi-hand-thumbs-up';
                    const downCls = c.reactionType === 'DISLIKE' ? 'bi-hand-thumbs-down-fill' : 'bi-hand-thumbs-down';
                    const childHtml = `
                    <div class="comment-item child mt-3" data-id="${c.commentId}">
                        <div class="comment-body">
                            <div class="comment-meta-row ms-3">
                                <div class="comment-avatar">
                                    <i class="bi bi-arrow-return-right me-2"></i>
                                    <img src="${c.profileImageUrl || '/images/default-profile.svg'}"
                                        alt="${c.writerName} 프로필 사진"
                                        class="avatar-img"/>
                                </div>
                                <div class="comment-meta-info">
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
                                    <span class="comment-date">
                                        ${formatDate(new Date(c.createdDate))}
                                        ${c.createdDate !== c.lastModifiedDate ? '*' : ''}
                                    </span>
                                </div>
                            </div>
                            <div class="comment-content mt-3 ms-5" data-original="${safe}">${safe}</div>
                            
                            <div class="comment-action-buttons d-flex align-items-center ms-4">
                                <button class="btn comment-thumbs-up">
                                    <i class="bi ${upCls}"></i>
                                </button>
                                <p class="reaction-ct">${c.totalLikesPlusTotalDislikes}</p>
                                <button class="btn comment-thumbs-down">
                                    <i class="bi ${downCls}"></i>
                                </button>
                            </div>
                        </div>
                    </div>`;
                    $cont.append(childHtml);
                });

                childPage++;
                $replyBtn.data('page', childPage);

                // 3) 더 불러올 게 남았으면 “더보기”, 아니면 원래 텍스트로 복귀
                if (!isLast) {
                    const $more = $(
                        `<button 
                              class="btn btn-sm load-more-child mt-2"
                              style="font-size: 16px; color: #0d6efd"
                         >
                                    <i class="bi bi-arrow-return-right"></i>&nbsp&nbsp답글 더보기
                         </button>`
                    );
                    $cont.append($more);
                }
            })
            .fail(err => {
                console.error('대댓글 로드 실패', err);
                $replyBtn.text(`답글 ${total}개`).prop('disabled', false);
            });
    }

    // ── 1) “답글 N개” 버튼 클릭 ──
    $(document).on('click', '.load-child-comments', function() {
        const $btn = $(this);
        const $cont = $btn.closest('.comment-body')
            .find('.child-comments-container');
        const total = $btn.data('total');

        // 이미 댓글이 렌더링 되어있으면 → 토글: 숨기고 page 초기화
        if ($cont.children().length) {
            $cont.empty();
            $cont.find('.load-more-child').remove();
            $btn.data('page', 0)
                .html(`<i class="bi bi-chevron-down ms-1"></i>&nbsp&nbsp답글 ${total}개</i>`);
            return;
        }

        // 처음이거나 숨겨진 상태에서 클릭하면 load
        $btn.data('page', 0);
        loadChildComments($btn);
    });

    // ── 2) “더보기” 버튼 클릭 ──
    $(document).on('click', '.load-more-child', function() {
        const $moreBtn  = $(this);
        const $replyBtn = $moreBtn.closest('.comment-body')
            .find('.load-child-comments');
        $moreBtn.remove();            // 이전 “더보기” 제거
        loadChildComments($replyBtn);
    });

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
                if (err.status === 401) alert('로그인이 필요합니다.');
                else {
                    alert('댓글 등록에 실패했습니다.');
                }
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
            data: JSON.stringify({content: raw}),
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
                if (err.status === 401) alert('로그인이 필요합니다.');
                else {
                    alert('댓글 수정에 실패했습니다.');
                }
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
                if (err.status === 401) alert('로그인이 필요합니다.');
                else {
                    alert('댓글 삭제에 실패했습니다.');
                }
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

    //답변 새로고침 아이콘이 클릭된 경우
    $('#comment-refresh-btn').click(function () {
        // 페이지 초기화
        page = 0;
        isLast = false;
        // 기존 댓글 지우기
        $('#comments-container').empty();
        loadComments();
    });

    // 1) "답글" 버튼 클릭 → 입력창 토글
    $(document).on('click', '.reply-comment-child-btn', function() {
        if (!isLoggedIn) {
            return alert('로그인이 필요합니다.');
        }
        const $btn  = $(this);
        const $body = $btn.closest('.comment-body');
        // 이미 열려 있으면 포커스만
        if ($body.find('.child-reply-area').length) {
            return $body.find('.child-reply-area').focus();
        }
        // textarea + 등록/취소 버튼 삽입
        const textareaPlaceholder = `${userName}님, 답글을 작성해보세요.`

        const $ta = $(`
            <textarea class="child-reply-area w-100 mt-2" style="border-radius: 8px; padding: 8px 12px;"
                      rows="10" placeholder="${textareaPlaceholder}"></textarea>
        `);

        const $btnGroup = $(`
            <div class="mt-2 d-flex justify-content-end w-100">
                <button class="btn btn-sm submit-child-reply" style="padding-right: 10px">저장</button>
                <button class="btn btn-sm cancel-child-reply">취소</button>
            </div>
        `);
        $btn.after($ta, $btnGroup);
        $ta.focus();
    });

    // 2) "취소" 버튼 클릭 → 입력창 제거
    $(document).on('click', '.cancel-child-reply', function() {
        const $body = $(this).closest('.comment-body');
        $body.find('.child-reply-area, .submit-child-reply, .cancel-child-reply').remove();
    });

    // 3) "등록" 버튼 클릭 → 답글 POST
    $(document).on('click', '.submit-child-reply', function() {
        if (!isLoggedIn) {
            return alert('로그인이 필요합니다.');
        }
        const $btn    = $(this);
        const $body   = $btn.closest('.comment-body');
        const raw     = $body.find('.child-reply-area').val().trim();
        if (!raw) return alert('답글 내용을 입력해주세요.');

        const parentId = $btn.closest('.comment-item').data('id');
        const $replyBtn = $body.find(`.load-child-comments[data-id="${parentId}"]`);
        const $cont     = $body.find('.child-comments-container');

        $btn.prop('disabled', true).text('등록중…');
        $.ajax({
            url: `/api/comment/${boardId}`,
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ content: raw, parentCommentId: parentId })
        })
            .done(() => {
                /*// 1) 입력창 + 버튼 제거
                $body.find('.child-reply-area, .submit-child-reply, .cancel-child-reply').remove();

                // 2) 기존에 렌더된 대댓글 전부 지우기
                $cont.empty();
                // 3) 더보기 버튼도 제거
                $cont.siblings('.load-more-child').remove();

                // 4) 페이지 리셋
                $replyBtn.data('page', 0);

                // 5) 다시 첫 10개부터 로드
                loadChildComments($replyBtn);*/

                page = 0;
                isLast = false;
                $('#comments-container').empty();
                loadComments();
            })
            .fail(err => {
                if (err.status === 401) alert('로그인이 필요합니다.');
                else {
                    alert('답글 등록에 실패했습니다.');
                }
            })
            .always(() => {
                $btn.prop('disabled', false).text('등록');
            });
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

$(document).on('click', '.menu-toggle', function (e) {
    e.stopPropagation();  // 외부 클릭 이벤트 막기
    const $menu = $(this).closest('.comment-menu');
    $('.comment-menu.open').not($menu).removeClass('open');
    $menu.toggleClass('open');
});

// 빈 공간 클릭 시 닫기
$(document).on('click', function () {
    $('.comment-menu.open').removeClass('open');
});