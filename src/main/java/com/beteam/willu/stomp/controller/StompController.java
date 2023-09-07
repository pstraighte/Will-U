package com.beteam.willu.stomp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class StompController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    /// 해당하는 방에 메세지를 보낸것
    @MessageMapping("/chat/send")
    @SendTo     //수신메세지를 어떤 topic 으로 보낼지
    //TODO topic 을 조금 더 의미있는 단어로 바꿀 수 있을 듯?
    public void sendMsg(@Payload Map<String, Object> data) {
        System.out.println("data = " + data);
        simpMessagingTemplate.convertAndSend("/topic/" + data.get("roomNumber"), data);

    }

    // 유저 개별 메세지
    @MessageMapping("/sendToUser/{username}")
    public void sendToUser(@DestinationVariable String username, @Payload Map<String, Object> data) {
        // 메시지를 특정 유저에게 보내는 로직
        simpMessagingTemplate.convertAndSend("/topic/" + data.get("sender"), data);
        System.out.println("data = " + data);
    }
}
