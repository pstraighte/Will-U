package com.beteam.willu.user.entity;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import com.beteam.willu.common.Timestamped;
import com.beteam.willu.notification.dto.NotificationRequestDto;
import com.beteam.willu.notification.entity.NotificationType;
import com.beteam.willu.user.dto.UserUpdateRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Timestamped {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	// 아이디
	@Column(name = "username", nullable = false, unique = true, length = 40)
	private String username;

	// 비밀번호
	@Column(name = "password", nullable = false)
	private String password;

	// 닉네임
	@Column(name = "nickname", nullable = false, unique = true, length = 40)
	private String nickname;

	// 이메일
	@Column(name = "email", nullable = false, unique = true)
	private String email;

	// 번호
	@Column(name = "phoneNumber")
	private String phoneNumber;

	// 지역
	@Column(name = "area")
	private String area;

	// 프로필 사진 url
	@Column(name = "picture")
	private String picture;

	// 기본 점수
	@Column(name = "score", nullable = false)
	@Builder.Default
	private Long score = 3L;

	@Column(name = "kakaoId")
	private Long kakaoId;

	@Column(name = "googleId")
	private String googleId;

	@Column(name = "naverId")
	private String naverId;

	@Transactional
	public void profileUpdate(UserUpdateRequestDto updateRequestDto) {
		this.nickname = updateRequestDto.getNickname();
		this.phoneNumber = updateRequestDto.getPhoneNumber();
		this.area = updateRequestDto.getArea();
	}

	// 카카오 id 를 업데이트
	// 해당 메소드를 사용한 곳에서 업데이트한 유저 데이터를 바로 사용
	public User kakaoIdUpdate(Long kakaoId) {
		this.kakaoId = kakaoId;
		return this;
	}

	public User googleIdUpdate(String googleId) {
		this.googleId = googleId;
		return this;
	}

	public User naverIdUpdate(String naverId) {
		this.naverId = naverId;
		return this;
	}

	public void setPicture(String url) {
		this.picture = url;
	}

	public void publishEvent(ApplicationEventPublisher eventPublisher, NotificationType type) {
		//type 에 따른 알림 내용 변경
		NotificationRequestDto requestDto = new NotificationRequestDto(this.getNickname() + "님이 로그인하셨습니다.", "로그인 알림",
			type, this);
		eventPublisher.publishEvent(requestDto);
	}
}

