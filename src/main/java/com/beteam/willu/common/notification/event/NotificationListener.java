package com.beteam.willu.common.notification.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.beteam.willu.common.notification.NotificationRequestDto;
import com.beteam.willu.common.notification.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationListener {

	private final NotificationService notificationService;

	@TransactionalEventListener
	@Async
	public void handleNotification(NotificationRequestDto requestDto) {
		notificationService.send(requestDto.getReceiver(), requestDto.getNotificationType(),
			requestDto.getContent(), requestDto.getTitle());
	}
}