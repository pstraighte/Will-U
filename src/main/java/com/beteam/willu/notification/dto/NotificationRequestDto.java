package com.beteam.willu.notification.dto;

import com.beteam.willu.notification.entity.NotificationType;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationRequestDto {
	private Long postId;
	private NotificationType type;
	private Long receiverId;
	private Long notificationId;
}
