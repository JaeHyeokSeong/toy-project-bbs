const MAX_SIZE = 200 * 1024 * 1024; // 200MB

$(document).ready(function () {
    $("#files").change(function () {
        let file_info = $("#file-info");
        let list = [];
        let total = 0;
        let display_total_file_size = 0;

        const files = this.files;
        for (let i = 0; i < files.length; i++) {
            const size = files[i].size;
            total += size;
            let file_size = (size / 1024 / 1024).toFixed(2);
            display_total_file_size += Number(file_size);
            list.push(files[i].name + ' (' + file_size + ' MB)');
        }

        let file_info_text = "";
        for (let i = 0; i < list.length; i++) {
            file_info_text += "첨부 파일 " + (i + 1) + ": " + list[i] + "<br>"
        }
        file_info.html(file_info_text);
        file_info.css("color", "#6c757d");

        let total_file_size = $("#total-file-size");
        total_file_size.text(display_total_file_size.toFixed(2) + "MB");
        total_file_size.css("color", "#000")

        let submit = $("#submit");
        submit.attr("disabled", false);
        submit.css("backgroundColor", "#0d6efd");
        submit.css("color", "#fff");

        if (total > MAX_SIZE) {
            total_file_size.css("color", "#dc3545")
            submit.attr("disabled", true);
            submit.css("backgroundColor", "grey");
            submit.css("color", "#fff");
        }
    });


    //첨부파일 수정 버튼
    $(".file-edit-btn").click(function () {
        $(".file-edit-div").css("display", "block");
        $(".file-div").css("display", "none");
        $("#updateFile").prop('checked', true);
    });

    $(".file-edit-cancel-btn").click(function () {
        $(".file-edit-div").css("display", "none");
        $(".file-div").css("display", "block");
        $("#files").val("");
        $("#total-file-size").text("0MB");
        $("#file-info").text("");
        $("#updateFile").prop('checked', false);
    });
});