$(document).ready(function () {

    $("#signup-bt").click(function () {
        const pathname = location.pathname;
        const search = location.search;

        const urlParams = new URLSearchParams(search);
        if (urlParams.has("redirectURL")) {
            location.href = "/signup" + search;
        } else {
            location.href = '/signup?redirectURL=' + encodeURIComponent(pathname + search);
        }
    });

    $("#login-bt").click(function () {
        const pathname = location.pathname;
        const search = location.search;

        const urlParams = new URLSearchParams(search);
        if (urlParams.has("redirectURL")) {
            location.href = "/login" + search;
        } else {
            location.href = '/login?redirectURL=' + encodeURIComponent(pathname + search);
        }
    });

    //로그아웃 버튼 클릭되어진 경우
    $('#logout-form').on('submit', function () {
        const pathname = location.pathname;
        const search = location.search;

        const redirectURL = $('#redirectURL');
        redirectURL.val(pathname + search);
    });
});