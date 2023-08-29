package com.beteam.willu.common.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.beteam.willu.notification.dto.NotificationEvent;
import com.beteam.willu.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationListener {

	private final NotificationService notificationService;

	@TransactionalEventListener
	@Async
	public void handleNotification(NotificationEvent event) {
		notificationService.send(event.getPublisher(), event.getReceiver(), event.getNotificationType(),
			event.getContent(), event.getTitle());
	}
}
