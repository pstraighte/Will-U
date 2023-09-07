package com.beteam.willu.stomp.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketConnectionManager {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(String userId, WebSocketSession session) {
        sessions.put(userId, session);
        System.out.println("this.sessions = " + this.sessions.get(userId));
    }

    public void removeSession(String userId) {
        sessions.remove(userId);
    }

    public void disconnectUser(String userId, CloseStatus closeStatus) throws IOException {
        WebSocketSession session = sessions.get(userId);
        if (session != null) {
            session.close(closeStatus);
        }
    }
}