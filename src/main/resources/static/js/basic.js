$(document).ready(function () {

    $("#signup-bt").click(function () {
        const pathname = location.pathname;
        const search = location.search;
        if (search !== '') {
            location.href = '/signup?redirectURL=' + encodeURIComponent(pathname + search);
        } else {
            location.href = '/signup?redirectURL=' + encodeURIComponent(pathname);
        }
    });

    $("#login-bt").click(function () {
        const pathname = location.pathname;
        const search = location.search;
        if (search !== '') {
            location.href = '/login?redirectURL=' + encodeURIComponent(pathname + search);
        } else {
            location.href = '/login?redirectURL=' + encodeURIComponent(pathname);
        }
    });
});