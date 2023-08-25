package com.beteam.willu.common.notification;

import com.beteam.willu.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationRequestDto {

	private String content;

	private String title;

	private NotificationType notificationType;

	private User receiver;
}
