$(document).ready(function () {

    $("#go-list").click(function () {
        location.href = '/bbs';
    });

    $("#delete-btn").click(function () {
        return confirm("게시물을 정말 삭제하겠습니까?");
    });
});