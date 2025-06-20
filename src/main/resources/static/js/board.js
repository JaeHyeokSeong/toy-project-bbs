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