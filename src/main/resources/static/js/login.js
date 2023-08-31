function googleLogin() {
    window.location.href = `https://accounts.google.com/o/oauth2/v2/auth?client_id=645290605461-os264ffg5u5a27545p76e535cuhf1v0u.apps.googleusercontent.com&redirect_uri=http://localhost:8080/api/users/login/oauth2/code/google&response_type=code&scope=email profile`
}

function kakaoLogin() {
    window.location.href = 'https://kauth.kakao.com/oauth/authorize?client_id=2f3499e63a763e115b8963e5669f7bdd&redirect_uri=http://localhost:8080/api/users/kakao/callback&response_type=code'
}

function naverLogin() {
    window.location.href = 'https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=0St_ZZCyosLX1RyQHKhs&redirect_uri=http://localhost:8080/api/users/naver/callback&response_type=code;'
}

function login() {
    $.ajax({
        url: `/api/users/login`, // 요청을 보낼 서버의 URL
        method: 'POST', // 요청 메소드 (GET, POST 등)
        contentType: "application/json",
        data: JSON.stringify({username: $("#idInput").val(), password: $("#passInput").val()}),
        success: function (response) {
            alert("로그인 성공")

            window.location.href = "/notification-page";
        },
        error: function (xhr, status, error) {
            alert("로그인 실패")
            console.log(xhr);
        }
    });
}
