package com.beteam.willu.notification.dto;

import com.beteam.willu.notification.entity.Notification;
import com.beteam.willu.notification.entity.NotificationType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationResponseDto {
	private Long id;

	private String title;

	private String content;

	private Boolean isRead;
	private NotificationType notificationType;

	private String nickname;

	public NotificationResponseDto(Notification notification) {
		this.id = notification.getId();
		this.content = notification.getContent();
		this.title = notification.getTitle();
		this.isRead = notification.getIsRead();
		this.notificationType = notification.getNotificationType();
		this.nickname = notification.getReceiver().getNickname();

	}
}
