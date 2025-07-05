$(document).ready(function () {

    $("#signup-bt").click(function () {
        const pathname = location.pathname;
        const search = location.search;

        const urlParams = new URLSearchParams(search);
        if (urlParams.has("redirectURL")) {
            location.href = "/signup" + search;
            return;
        }

        if (search !== '') {
            location.href = '/signup?redirectURL=' + encodeURIComponent(pathname + search);
        } else {
            location.href = '/signup?redirectURL=' + encodeURIComponent(pathname);
        }
    });

    $("#login-bt").click(function () {
        const pathname = location.pathname;
        const search = location.search;

        const urlParams = new URLSearchParams(search);
        if (urlParams.has("redirectURL")) {
            location.href = "/login" + search;
            return;
        }

        if (search !== '') {
            location.href = '/login?redirectURL=' + encodeURIComponent(pathname + search);
        } else {
            location.href = '/login?redirectURL=' + encodeURIComponent(pathname);
        }
    });

    //로그아웃 버튼 클릭되어진 경우
    $('#logout-form').on('submit', function () {
        const pathname = location.pathname;
        const search = location.search;

        const redirectURL = $('#redirectURL');
        redirectURL.val(encodeURIComponent(pathname + search));
    });
});