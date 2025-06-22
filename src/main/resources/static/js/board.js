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
            data: JSON.stringify({ reactionType: 'LIKE' }),
            success: function(response) {
                $("#total-reaction-ct").text(response.totalReactionCounts);
            },
            error: function(xhr, status, error) {
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
            data: JSON.stringify({ reactionType: 'DISLIKE' }),
            success: function(response) {
                $("#total-reaction-ct").text(response.totalReactionCounts);
            },
            error: function(xhr, status, error) {
                alert('좋아요 응답이 실패했습니다. 나중에 다시 시도해 주세요.');
            }
        });
    });
});

//답변
$(function() {
    const boardId = $("#board-id").text().trim();
    let page    = 0;
    let size    = 10;
    let loading = false;
    let hasNext = true;

    //1) 총 답변 개수 조회 및 업데이트
    function updateCommentCount() {
        $.ajax({
            url: `/api/comments-parent-total-count/${boardId}`,
            method: 'GET'
        })
            .done(dto => {
                // dto.totalCount를 가져와 텍스트 업데이트
                $("#comment-count").text(`답변 ${dto.totalCount}개`);
            })
            .fail(err => {
                console.error("총 댓글 개수 로드 실패", err);
            });
    }

    //2) 답변 불러오기 (페이징 + 무한 스크롤)
    function loadComments() {
        if (!hasNext || loading) return;
        loading = true;
        $("#loading-spinner").show();

        $.ajax({
            url: `/api/comments-parent/${boardId}`,
            method: 'GET',
            data: { page, size }
        })
            .done(data => {
                // 첫 페이지에 댓글이 하나도 없을 때
                if (page === 0 && data.content.length === 0) {
                    $("#comments-container").html(`
                    <div class="no-comments">
                        답변을 기다리고 있는 게시물이에요<br>
                        첫번째 답변을 남겨보세요!
                    </div>`);
                    hasNext = false;
                    return;
                }

                // 댓글 아이템 추가
                data.content.forEach(c => {
                    const created = new Date(c.createdDate).toLocaleString();
                    const content = $('<div>').text(c.content).html();

                    const html = `
                    <div class="comment-item">
                        <div class="comment-body">
                            <div class="comment-header">
                                <span class="comment-author">${c.name}</span>
                                <span class="comment-date">${created}</span>
                            </div>
                            <div class="comment-content">${content}</div>
                            <button class="reply-comment-child-btn">답글</button>
                        </div>                       
                    </div>`;
                    $("#comments-container").append(html);
                });

                hasNext = !data.last;
                page++;
            })
            .fail(err => {
                console.error("답변 로드 실패", err);
            })
            .always(() => {
                $("#loading-spinner").hide();
                loading = false;
            });
    }

    //3) textarea 줄 수 자동 조절 (최소 1)
    function adjustRows() {
        const ta = document.getElementById('add-comment-parent');
        const lines = ta.value.split('\n').length;
        ta.rows = Math.max(1, lines);
    }

    //4) 입력창 포커스/클릭/입력 시 버튼 보이기 & 줄 수 조절
    $('#add-comment-parent').on('focus click input', function() {
        $('#submit-comment-parent, #cancel-comment-parent').show();
        adjustRows();
    });

    //5) 취소 버튼: 입력 초기화 & 버튼 숨기기
    $('#cancel-comment-parent').on('click', function() {
        const $ta = $('#add-comment-parent');
        $ta.val('').attr('rows', 1).blur();
        $('#submit-comment-parent, #cancel-comment-parent').hide();
    });

    //6) 답변 등록 버튼: POST 후 리셋 & 재로딩
    $('#submit-comment-parent').on('click', function() {
        const comment_parent_val = $('#add-comment-parent').val();
        let content = comment_parent_val.trim();
        if (!content) {
            alert('답변 내용을 입력해주세요.');
            return;
        }

        content = comment_parent_val;
        $.ajax({
            url: `/api/comment-parent/${boardId}`,
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ content })
        })
            .done(newC => {
                // 리스트 초기화 + 첫 페이지부터 다시 로드
                page    = 0;
                hasNext = true;
                $('#comments-container').empty();
                loadComments();

                // 총 댓글 수 갱신
                updateCommentCount();

                // 입력창 초기화 & 버튼 숨기기
                $('#add-comment-parent').val('').attr('rows', 1).blur();
                $('#submit-comment-parent, #cancel-comment-parent').hide();
            })
            .fail(err => {
                console.error('댓글 등록 실패', err);
                alert('댓글 등록에 실패했습니다.');
            });
    });

    //7) 페이지 전체 스크롤 감지 → 추가 로드
    $(window).on('scroll', function() {
        if ($(window).scrollTop() + $(window).height() >= $(document).height() - 100) {
            loadComments();
        }
    });

    //초기 로드
    loadComments();
    updateCommentCount();
});