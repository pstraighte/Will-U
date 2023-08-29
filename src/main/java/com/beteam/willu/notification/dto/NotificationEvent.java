package com.beteam.willu.notification.dto;

import com.beteam.willu.notification.entity.NotificationType;
import com.beteam.willu.user.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
//이벤트 객체
public class NotificationEvent {

	private String content;

	private String title;

	private NotificationType notificationType;

	private User receiver;
	private User publisher;
}
