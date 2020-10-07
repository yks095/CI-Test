$("#sign_up_btn").click( function (event) {
        const jsonData = JSON.stringify({
            email: $('#sign_up_email').val(),
            password: $('#sign_up_password').val(),
        })

        const token = $("meta[name='_csrf']").attr("content");
        const header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: "/sign-up",
            type: "POST",
            data: jsonData,
            contentType: "application/json",
            dataType: "json",
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (data) {
                alert(data['msg']);
                location.href = '/';
            },
            error: function (data) {
                alert(data.responseText);
            }
        });

    }
);