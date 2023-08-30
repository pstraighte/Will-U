package com.beteam.willu.notification.dto;

import com.beteam.willu.notification.entity.NotificationType;

import lombok.Getter;

@Getter
public class NotificationRequestDto {
	private Long postId;
	private NotificationType type;
}
