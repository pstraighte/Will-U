package com.beteam.willu.notification.entity;

public enum NotificationType {
	MAKE_CONNECTION("SSE 연결"),
	JOIN_REQUEST("참가 신청"),
	APPROVE_REQUEST("참가 승인"),
	REJECT_REQUEST("참가 거절"),
	RECRUIT_DONE("인원 모집 완료"),
	LOGIN_DONE("로그인 완료 알림");

	private final String description;

	NotificationType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
