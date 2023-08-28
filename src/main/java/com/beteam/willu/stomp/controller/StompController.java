package com.beteam.willu.stomp.controller;

import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StompController {
	private final SimpMessagingTemplate simpMessagingTemplate;

	/// 해당하는 방에 메세지를 보낸것
	@MessageMapping("/chat/send")
	@SendTo     //수신메세지를 어떤 topic 으로 보낼지
	//TODO topic 을 조금 더 의미있는 단어로 바꿀 수 있을 듯?
	public void sendMsg(@Payload Map<String, Object> data) {
		simpMessagingTemplate.convertAndSend("/topic/" + data.get("roomNumber"), data);
		System.out.println("data = " + data);
	}
}
