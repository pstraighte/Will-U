package com.beteam.willu.notification.entity;

public enum NotificationType {
	//TODO 알림이 필요한 상태마다 추가해주고 상태에 맞는 알림을 자동으로 생성할 수 있도록 수정 필요
	MAKE_CONNECTION("SSE 연결"),
	JOIN_REQUEST("참가 신청"),
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
