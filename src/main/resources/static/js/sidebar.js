const token = getAuthorizationCookie();
if (token !== null) {
    const payloads = JSON.parse(atob(token.split(".")[1]));
    const userName = payloads.sub;
    console.log(token);
    console.log(userName);
    getChatRooms(userName);
}

checkLoginStatus(token);
//채팅방 조회


$(".accordion-header").click(function () {
    $(this).toggleClass("active");
    $(this).next(".accordion-content").slideToggle();
    $(".accordion-content").not($(this).next()).slideUp();
    $(".accordion-header").not($(this)).removeClass("active");
});
showMyNotification();

const eventSource = new EventSource(`/subscribe`);

const eventList = $(".alert-list");
let ntId;

eventSource.onopen = e => {
    console.log("연결 완료");
    console.log(e);
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
        const postId = jsonData.postId;
        console.log("ntId: " + ntId);
        console.log("publisherId: " + publisherId);
        console.log("postId: " + postId);
        showNotification(content, nt, title, ntId);
        addNotificationHTML(content, nt, title, ntId, publisherId, postId);

    }
}
eventSource.onerror = error => {
    console.log("에러 발생");
    //eventSource.close();
};

//알림 허용 체크
notifyMe();


function getChatRooms(userName) {
    $.ajax({
        url: `/api/chat/getUsers/${userName}`, // 쿠키에 저장된 사용자의 이름으로 사용자 id 가져오기
        method: 'GET', // 요청 메소드 (GET, POST 등)
        success: function (response) {
            console.log("response1 : ", response);

            // response 사용자의 id
            $.ajax({
                url: `/api/chat/users/${response}`, // 가져온 사용자의 id로 사용자가 속한 채팅방들 조회
                method: 'GET', // 요청 메소드 (GET, POST 등)
                success: function (response) {

                    console.log("response2 : ", response);

                    if (response.chatRoomList.length == 0) {
                        // 해당 사용자가 속한 채팅방이 없다면

                    }

                    // 해당 사용자가 속한 채팅방이 있다면
                    for (let i = 0; i < response.chatRoomList.length; i++) {
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


function openSidebar() {
    let sidebar = document.getElementById("sidebar");
    let content = document.getElementById("content");
    let openBtn = document.getElementById("openBtn");
    let closeBtn = document.getElementById("closeBtn");

    if (sidebar.style.width === "250px") {
        sidebar.style.width = "0";
        content.style.marginLeft = "0";
        closeBtn.style.display = "none";
        openBtn.style.display = "inline-block";
    } else {
        sidebar.style.width = "250px";
        content.style.marginLeft = "260px";
        closeBtn.style.display = "inline-block";
        openBtn.style.display = "none";
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
            window.location.href = `/users/profile/${response}`;
        },
        error: function (xhr, status, error) {
            alert("불러오기 실패")
            console.log(xhr);
        }
    });

}

function chatRoom(id) {
    // 해당 채팅방의 id 전달 (채팅방 입장)
    window.location.href = `/users/chat?number=${id}`;
}


//알림 기능

function read(ntId) {
    $.ajax({
        url: `/api/notification/${ntId}`, // 요청을 보낼 서버의 URL
        method: 'PATCH', // 요청 메소드 (GET, POST 등)
        contentType: "application/json",
        success: function (response) {
            console.log("읽기 처리 완료");
            //TODO 알림이 포함된 HTML 지우기
            removeNotificationHTML(ntId);
        },
        error: function (xhr, status, error) {
            console.log("읽기 처리 실패");
        }
    });
}

function reject(ntId, postId, publisherId) {
    if (confirm("참가 요청을 거부하시겠습니까?")) {
        console.log("거부했습니다.");

        sendReject(ntId, postId, publisherId);
        read(ntId);
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
        read(ntId);
        //chatroom user 추가
        //window.location.reload();
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
        // 알림 클릭 시 수행할 동작 설정
        window.focus();
        //사이드바 열고 알림 목록 보여주기

        notification.close();

    };
}

function addNotificationHTML(content, nt, title, ntId, publisherId, postId) {
    let newElement =
        `<div class="unread-notification-${ntId}">
                    <h2>title: ${title}</h2>
                    <p>content: ${content}</p>
                    <p>type: ${nt}</p>
                `;
    if (nt === "JOIN_REQUEST") {
        newElement += `<input type="button" onclick="approve(${ntId},${postId},${publisherId})" value="승인">
                                <input type="button" onclick="reject(${ntId},${postId},${publisherId})" value="거부">`
    } else {
        newElement += `<input type="button" onclick="read(${ntId})" value="닫기">`;
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
        "receiverId": publisherId
    };
    //헤더에 토큰
    $.ajax({
        url: `/api/notifications`, // 요청을 보낼 서버의 URL
        method: 'POST', // 요청 메소드 (GET, POST 등)
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (response) {
            alert("전송 완료");

        },
        error: function (xhr, status, error) {
            alert("저장 실패")
            console.log(xhr);
        }
    });
}

function sendApprove(ntId, postId, publisherId) {
    console.log("승인 전송");
    const data = {
        "postId": postId,
        "userId": publisherId
    };
    //승인 알림 전송
    $.ajax({
        url: `/api/chatRoom/join`, // 요청을 보낼 서버의 URL
        method: 'POST', // 요청 메소드 (GET, POST 등)
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (response) {
            alert("전송 완료");

        },
        error: function (xhr, status, error) {
            alert("저장 실패")
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
                    const publisherId = notification.userId;
                    const postId = notification.postId;
                    console.log("ntId: " + ntId);
                    console.log("publisherId: " + publisherId);
                    console.log("postId: " + postId);
                    addNotificationHTML(content, nt, title, ntId, publisherId, postId);
                }
            });
        },
        error: function (xhr, status, error) {
            console.log(xhr);
        }
    });
}