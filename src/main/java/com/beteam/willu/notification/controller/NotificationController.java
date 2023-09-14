package com.beteam.willu.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.beteam.willu.common.ApiResponseDto;
import com.beteam.willu.common.security.UserDetailsImpl;
import com.beteam.willu.notification.dto.NotificationRequestDto;
import com.beteam.willu.notification.dto.NotificationResponseDto;
import com.beteam.willu.notification.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Notification API", description = "알림 관련 API")
@RestController
@RequiredArgsConstructor
public class NotificationController {
	private final NotificationService notificationService;

	//현재 로그인한 ID에 대한(본인) 구독으로 EventStream 생성 하도록 함. 후에 수정 여지 있음
	@Operation(summary = "사용자 구독", description = "현재 로그인한 ID에 대한(본인) 구독으로 EventStream 생성 하도록 한다.")
	@GetMapping(value = "/subscribe", produces = "text/event-stream")
	public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
		return notificationService.subscribe(userDetails.getUser().getId(), lastEventId);
	}

	//알림 읽음 상태 수정 안읽음 <-> 읽음
	@Operation(summary = "알림 상태 제어", description = "PathVariable형태의 알림 id를 이용해 해다 알림의 상태를 수정한다 (안읽음 <-> 읽음)")
	@PatchMapping("/api/notification/{id}")
	public ResponseEntity<ApiResponseDto> updateNotification(@PathVariable long id) {
		notificationService.updateRead(id);
		return ResponseEntity.ok().body(new ApiResponseDto("읽음 상태 처리 완료", 200));
	}

	@Operation(summary = "모집글 알림 참가 (승인/거절)", description = "정해진 파라미터를 값을 이용해 신청자가 보낸 알림을 모집자 입장에서 참가를 승인할지 거절할지 결정한다.")
	@PostMapping("/api/notifications")
	public ResponseEntity<ApiResponseDto> sendJoinNotifcation(@RequestBody NotificationRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {

		if (requestDto.getNotificationId() != null) {
			notificationService.updateRead(requestDto.getNotificationId());
		}

		notificationService.sendRequestNotification(requestDto, userDetails.getUser());
		return ResponseEntity.ok().body(new ApiResponseDto("알림 전송 완료", 200));
	}

	@Operation(summary = "알림 보여주기", description = "사용작가 받은 알림을 보여준다.")
	@GetMapping("/api/notifications")
	public ResponseEntity<List<NotificationResponseDto>> showNotifications(
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.ok(notificationService.getNotificationByUserId(userDetails.getUser().getId()));
	}
}
