package com.beteam.willu.notification;

public enum NotificationType {
	JOIN_REQUEST("채팅방 참가 신청"),
	RECRUIT_DONE("인원 모집 완료"),
	LOGIN_DONE("로그인 완료 알림 for test");

	private final String description;

	NotificationType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
