package com.beteam.willu.stomp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Service
@RequiredArgsConstructor
public class WebSocketDisconnectListener {
    private final WebSocketConnectionManager webSocketConnectionManager;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());

//        System.out.println("headers = " + headers.getHeader("simpSession"));
        // 연결된 클라이언트의 세션 ID 및 사용자 정보를 출력
//        System.out.println("WebSocket 연결 세션 ID: " + headers.getSessionId());
//        System.out.println("연결된 사용자: " + headers.getUser().getName());

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("headerAccessor = " + headerAccessor);

        String sessionId = headerAccessor.getSessionId();

        // 세션 ID를 사용하여 개별 유저의 연결을 해제하거나 관련 작업을 수행할 수 있음
        // 예: 사용자 정보 제거, 상태 업데이트 등
//        System.out.println("유저의 WebSocket 연결이 해제되었습니다. 세션 ID: " + sessionId);
    }
}