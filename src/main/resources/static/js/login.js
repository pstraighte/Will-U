function googleLogin() {
    window.location.href = `https://accounts.google.com/o/oauth2/v2/auth?client_id=645290605461-rr40njqgkvh758r32q783es0oflajmh2.apps.googleusercontent.com&redirect_uri=http://3.34.45.89:8080/api/users/login/oauth2/code/google&response_type=code&scope=email profile`
}


function kakaoLogin() {
    window.location.href = 'https://kauth.kakao.com/oauth/authorize?client_id=2f3499e63a763e115b8963e5669f7bdd&redirect_uri=http://3.34.45.89:8080/api/users/kakao/callback&response_type=code'
}

function naverLogin() {
    window.location.href = 'https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=0St_ZZCyosLX1RyQHKhs&redirect_uri=http://3.34.45.89:8080/api/users/naver/callback&response_type=code;'
}

$(document).ready(function () {
    // 토큰 삭제
    Cookies.remove('Authorization', {path: '/'});
    Cookies.remove('RT', {path: '/'});
});

function login() {
    let username = document.getElementById("idInput").value;
    let password = document.getElementById("passInput").value;
    if (username === '' || password === '') {
        alert("정보를 모두 입력해주세요");
        return;
    }
    $.ajax({
        url: `/api/users/login`, // 요청을 보낼 서버의 URL
        method: 'POST', // 요청 메소드 (GET, POST 등)
        contentType: "application/json",
        data: JSON.stringify({username: username, password: password}),

        success: function (response) {
            console.log(response.message);
            if (response.message === "로그인 성공") {
                const Toast = Swal.mixin({
                    toast: true,
                    position: 'center',
                    showConfirmButton: false,
                    timer: 1000,
                    timerProgressBar: true,
                    didOpen: (toast) => {
                        toast.addEventListener('mouseenter', Swal.stopTimer)
                        toast.addEventListener('mouseleave', Swal.resumeTimer)
                    }
                })

                Toast.fire({
                    icon: 'success',
                    title: '로그인 성공'
                }).then(() => {
                    window.location.href = "/";
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: '로그인 실패',
                    text: response,
                });
            }
        },
        error: function (xhr) {
            Swal.fire({
                icon: 'error',
                title: '로그인 실패',
                text: "아이디 혹은 비밀번호를 확인하세요",
            });
        }
    });
}

function signup() {
    let username = document.getElementById("username").value;
    let nickname = document.getElementById("nickname").value;
    let email = document.getElementById("email").value;
    let password = document.getElementById("password").value;
    let confirmPassword = document.getElementById("password-confirm").value;

    if (username === '' || password === '' || nickname === '' || email === '' || confirmPassword === '') {
        Swal.fire({
            icon: 'warning',
            title: '회원가입 실패',
            text: "회원 정보를 모두 입력해주세요"
        });
    }

    let nickname_format = '^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$';

    if (!nickname.match(nickname_format)) {
        Swal.fire({
            icon: 'warning',
            title: '회원가입 실패',
            text: "닉네임은 특수문자 없이 두글자 이상 입력해 주세요"
        });
        document.getElementById("nickname").style.color = "red"; // 일치하지 않을 경우 글씨 색을 빨간색으로 변경
        return;
    }


    let email_format = '^([0-9a-zA-Z_\.-]+)@([0-9a-zA-Z_-]+)(\\.[0-9a-zA-Z_-]+)$';

    if (!email.match(email_format)) {
        Swal.fire({
            icon: 'warning',
            title: '회원가입 실패',
            text: "아이디가 이메일 형식에 부합하지 않습니다."
        });
        document.getElementById("email").style.color = "red"; // 일치하지 않을 경우 글씨 색을 빨간색으로 변경
        return;
    }

    let password_format = '(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{6,16}'

    if (!password.match(password_format)) {
        Swal.fire({
            icon: 'warning',
            title: '회원가입 실패',
            text: "비밀번호는 영어대소문자 와 특수문자를 포함해 주세요."
        });
        document.getElementById("password").style.color = "red"; // 일치하지 않을 경우 글씨 색을 빨간색으로 변경
        return;
    }

    if (password !== confirmPassword) {
        Swal.fire({
            icon: 'warning',
            title: '회원가입 실패',
            text: "비밀번호가 일치하지 않습니다"
        });
        document.getElementById("password-confirm").style.color = "red"; // 일치하지 않을 경우 글씨 색을 빨간색으로 변경
        return;
    }

    $.ajax({
        type: "POST",
        url: "/api/users/signup",
        data: JSON.stringify({
            username: username,
            nickname: nickname,
            email: email,
            password: password
        }),
        contentType: "application/json",

        success: function (response) {
            const Toast = Swal.mixin({
                toast: true,
                position: 'center-center',
                showConfirmButton: false,
                timer: 800,
                timerProgressBar: true,
                didOpen: (toast) => {
                    toast.addEventListener('mouseenter', Swal.stopTimer)
                    toast.addEventListener('mouseleave', Swal.resumeTimer)
                }
            })

            Toast.fire({
                icon: 'success',
                title: '회원가입 성공'
            }).then(() => {
                window.location.href = "/login";
            });

        },
        error: function (xhr) {
            if (xhr.responseText) {
                try {
                    const responseJson = JSON.parse(xhr.responseText);
                    const errorMessage = responseJson.message;
                    Swal.fire({
                        icon: 'error',
                        title: '회원가입 실패',
                        text: errorMessage,
                    });
                } catch (e) {
                    Swal.fire({
                        icon: 'error',
                        title: '회원가입 실패'
                    });
                }
            }
        }
    })
}


$('#idDiv').keypress(function (e) {
    if (e.keyCode === 13) login();
});

$('#sinupDiv').keypress(function (e) {
    if (e.keyCode === 13) signup();
});

