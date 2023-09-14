const token = getAuthorizationCookie();

$("#logo").click(function () {
    window.location.href = '/';
});


if (token !== null) {
    const payloads = JSON.parse(atob(token.split(".")[1]));
    const userName = payloads.sub;
    console.log(token);
    console.log(userName);
    getChatRooms(userName);

}

checkLoginStatus(token);


$(".accordion-header").click(function () {
    $(this).toggleClass("active");
    $(this).next(".accordion-content").slideToggle();
    $(".accordion-content").not($(this).next()).slideUp();
    $(".accordion-header").not($(this)).removeClass("active");
});
$(".accordion-header2").click(function () {
    $(this).toggleClass("active");
    $(this).next(".accordion-content2").slideToggle();
    $(".accordion-content2").not($(this).next()).slideUp();
    $(".accordion-header2").not($(this)).removeClass("active");
});
showMyNotification();

const eventSource = new EventSource(`/subscribe`);

const eventList = $(".alert-list");
let ntId;

eventSource.onopen = e => {
    console.log("연결 완료");
};

eventSource.onmessage = e => {
    console.log(e.lastEventId);
    console.log("ON MESSAGE 실행");
    const {data: receivedConnectionData} = e;

    const jsonData = JSON.parse(receivedConnectionData);
    console.log(jsonData);
    ntId = jsonData.id;
    const nt = jsonData.notificationType;
    const title = jsonData.title;
    const content = jsonData.content;
    // e.lastEventId = ntId;

    if (nt !== "MAKE_CONNECTION") {
        const publisherId = jsonData.publisher.id;
        const receiverId = jsonData.receiver.id;
        const postId = jsonData.postId;
        showNotification(content, nt, title, ntId);
        addNotificationHTML(content, nt, title, ntId, publisherId, receiverId, postId);

        if (jsonData.notificationType == "APPROVE_REQUEST") {
            $('.accordion-content').empty();
            getChatRooms(jsonData.receiver.username);
        }
        //사이드바 열기
        var offcanvasTarget = $('#offcanvasWithBothOptions');
        if (!offcanvasTarget.hasClass('show')) {
            // 열려있는 경우 닫기
            offcanvasTarget.offcanvas('show');
        }
        if (!$(".accordion-header2").hasClass("active")) {
            $(".accordion-header2").click();
        }
    }
}
eventSource.onerror = error => {
    console.log("에러 발생");

};
window.onunload = function () {
    eventSource.close();
};
//알림 허용 체크
notifyMe();

function getChatRooms(userName) {
    $.ajax({
        url: `/api/chat/getUsers/${userName}`, // 쿠키에 저장된 사용자의 이름으로 사용자 id 가져오기
        method: 'GET', // 요청 메소드 (GET, POST 등)
        success: function (response) {
            // response 사용자의 id
            $.ajax({
                url: `/api/chat/users/${response}`, // 가져온 사용자의 id로 사용자가 속한 채팅방들 조회
                method: 'GET', // 요청 메소드 (GET, POST 등)
                success: function (response) {
                    if (response.chatRoomList.length === 0) {
                        // 해당 사용자가 속한 채팅방이 없다면
                    }
                    // 해당 사용자가 속한 채팅방이 있다면
                    for (var i = 0; i < response.chatRoomList.length; i++) {
                        $(`<div id="userChatRoom-${response.chatRoomList[i].id}" class="userChatRoom" onclick="chatRoom(${response.chatRoomList[i].id})">${response.chatRoomList[i].chatName}</div>`).appendTo(`.accordion-content`);
                    }
                },
                error: function (xhr, status, error) {
                    console.log(xhr);
                }
            });
        },
        error: function (xhr, status, error) {
            console.log(xhr);
        }
    });
}

// 쿠키에서 Authorization 값을 가져오는 함수
function getAuthorizationCookie() {
    let cookies = document.cookie.split(';');
    for (let i = 0; i < cookies.length; i++) {
        let cookie = cookies[i].trim();
        if (cookie.startsWith('Authorization=')) {
            return cookie.substring('Authorization='.length, cookie.length);
        }
    }
    return null;
}

// 페이지 로드 시 실행되는 함수
function checkLoginStatus(authorizationToken) {

    let profileBtnContainer = document.querySelector('.profile-btn-container');
    let loginBtnContainer = document.querySelector('.login-btn-container');

    if (authorizationToken != null) {
        console.log("로그인 완료 상태");
        profileBtnContainer.style.display = 'block';
        loginBtnContainer.style.display = 'none';
    } else {
        // 로그인 전 상태
        console.log("로그인 전 상태");
        profileBtnContainer.style.display = 'none';
        loginBtnContainer.style.display = 'block';
    }
}

function logout() {
    //로그아웃 api 호출하고 로그인 페이지로
    $.ajax({
        url: `/api/users/logout`, // 요청을 보낼 서버의 URL
        method: 'POST', // 요청 메소드 (GET, POST 등)
        contentType: "application/json",
        success: function (response) {
            window.location.href = "/";

        },
        error: function (xhr, status, error) {
            alert("로그아웃 실패")
            console.log(xhr);
        }
    });
}

function goProfile() {

    let authorizationToken = getAuthorizationCookie();

    const payloads = JSON.parse(atob(authorizationToken.split(".")[1]));
    const userName = payloads.sub;


    console.log("userName : ", userName);

    $.ajax({
        url: `/api/chat/getUsers/${userName}`, // 쿠키에 저장된 사용자의 이름으로 사용자 id 가져오기
        method: 'GET', // 요청 메소드 (GET, POST 등)
        success: function (response) {
            // response 사용자의 id
            window.location.href = `/profile/${response}`;
        },
        error: function (xhr, status, error) {
            alert("프로필 페이지 이동 실패")
            console.log(xhr);
        }
    });
}

function goMypage() {
    window.location.href = `/mypage`;
}

function chatRoom(id) {
    // 해당 채팅방의 id 전달 (채팅방 입장)
    window.location.href = `/users/chat?number=${id}`;
}


//알림 기능


function reject(ntId, postId, publisherId) {
    if (confirm("참가 요청을 거부하시겠습니까?")) {
        console.log("거부했습니다.");
        //거절 알림 전송
        sendReject(ntId, postId, publisherId);
        //read(ntId); //읽기를 거절 알림 보내기에 추가
        removeNotificationHTML(ntId);
        //거절 알림 전송
        //window.location.reload();
    } else {
        window.focus();
    }
}

function approve(ntId, postId, publisherId) {
    if (confirm("참가 요청을 승인하시겠습니까?")) {
        console.log("승인했습니다.");

        sendApprove(ntId, postId, publisherId);
        // read(ntId);
        removeNotificationHTML(ntId);
    } else {
        window.focus();
    }
}

function notifyMe() {
    if (Notification.permission === "granted") {
        console.log("notification granted");
    } else if (Notification.permission !== "denied") {
        Notification.requestPermission();
    }
}

function showNotification(content, notificationType, title, ntId) {
    const notificationOptions = {
        body: notificationType + ": " + content,
        icon: "path/to/icon.png" // 사용자에게 보일 아이콘의 경로
    };

    const notification = new Notification(title, notificationOptions);

    notification.onclick = function () {
        window.focus();
        notification.close();

    };
}

function addNotificationHTML(content, nt, title, ntId, publisherId, receiverId, postId) {
    let newElement =
        `<div class="unread-notification-${ntId}">
                    <h4>${title}</h4>
                    <p>${content}</p>
                    <p>${nt}</p>
                `;
    if (nt === "JOIN_REQUEST") {
        newElement += `<input type="button" class="btn btn-primary" onclick="approve(${ntId},${postId},${publisherId})" value="승인"  style="background-color:#1746A2">
                                <input type="button" class="btn btn-primary" onclick="reject(${ntId},${postId},${publisherId})" value="거부" style="background-color:#1746A2">`
    } else {
        newElement += `<input type="button" class="btn btn-primary" onclick="removeNotificationHTML(${ntId})" value="닫기" style="background-color:#1746A2">`;
    }
    newElement += `</div>`;
    eventList.append(newElement);
}

function removeNotificationHTML(ntId) {
    const div = $(`.unread-notification-${ntId}`);
    div.remove();
}

function sendReject(ntId, postId, publisherId) {
    const data = {
        "postId": postId,
        "type": "REJECT_REQUEST",
        "receiverId": publisherId,
        "notificationId": ntId
    };
    //헤더에 토큰
    $.ajax({
        url: `/api/notifications`, // 요청을 보낼 서버의 URL
        method: 'POST', // 요청 메소드 (GET, POST 등)
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (response) {
            // alert("전송 완료");
            console.log("전송 완료");
            console.log(response);

        },
        error: function (xhr, status, error) {
            // alert("전송 실패")
            console.log(xhr);
            console.log(error)
        }
    });
}

function sendApprove(ntId, postId, publisherId) {
    console.log("승인 전송");
    const data = {
        "postId": postId,
        "userId": publisherId,
        "notificationId": ntId
    };
    console.log(data);
    //승인 알림 전송
    $.ajax({
        url: `/api/chatRoom/join`, // 요청을 보낼 서버의 URL
        method: 'POST', // 요청 메소드 (GET, POST 등)
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (response) {
            console.log("승인 알림 전송 성공");
        },
        error: function (xhr, status, error) {
            alert("전송 실패")
            console.log(xhr);
        }
    });
}

function showMyNotification() {
    $.ajax({
        url: `/api/notifications`, // 요청을 보낼 서버의 URL
        method: 'GET', // 요청 메소드 (GET, POST 등)
        contentType: "application/json",
        success: function (data) {
            data.forEach(notification => {
                console.log(notification);
                ntId = notification.id;
                const nt = notification.notificationType;
                const title = notification.title;
                const content = notification.content;
                // e.lastEventId = ntId;

                if (nt !== "MAKE_CONNECTION") {
                    const publisherId = notification.publisherId;
                    const receiverId = notification.receiverId;
                    const postId = notification.postId;
                    addNotificationHTML(content, nt, title, ntId, publisherId, receiverId, postId);
                }
            });
        },
        error: function (xhr, status, error) {
            console.log(xhr);
        }
    });
}
