package com.beteam.willu.notification.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.beteam.willu.notification.Notification;
import com.beteam.willu.notification.NotificationType;
import com.beteam.willu.notification.repository.EmitterRepository;
import com.beteam.willu.notification.repository.NotificationRepository;
import com.beteam.willu.user.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
	private static final Long DEFAULT_TIMEOUT = 60 * 60L * 1000; //테스트를 위해
	private final EmitterRepository emitterRepository;
	private final NotificationRepository notificationRepository;

	public SseEmitter subscribe(Long userId, String lastEventId) {
		log.info("SSE subscribe");

		String emitterId = makeTimeIncludeId(userId);

		SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

		emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
		emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

		String eventId = makeTimeIncludeId(userId);

		// 503 에러를 방지하기 위한 더미 이벤트 전송
		sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + userId + "]");

		// 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
		if (hasLostData(lastEventId)) {
			log.info("미수신 데이터 있음");
			sendLostData(lastEventId, userId, emitterId, emitter);
		}
		return emitter;
	}

	public void send(User receiver, NotificationType notificationType, String content, String title) {
		log.info("send 실행");
		Notification notification = notificationRepository.save(
			createNotification(receiver, notificationType, content, title));

		String receiverId = String.valueOf(receiver.getId());
		String eventId = receiverId + "_" + System.currentTimeMillis();
		Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiverId);
		emitters.forEach(
			(key, emitter) -> {
				emitterRepository.saveEventCache(key, notification);
				sendNotification(emitter, eventId, key, notification);
			}
		);
	}

	private Notification createNotification(User receiver, NotificationType notificationType, String content,
		String title) {
		return Notification.builder()
			.receiver(receiver)
			.notificationType(notificationType)
			.content(content)
			.title(title)
			.isRead(false)
			.build();
	}

	private boolean hasLostData(String lastEventId) {
		return !lastEventId.isEmpty();
	}

	private void sendLostData(String lastEventId, Long memberId, String emitterId, SseEmitter emitter) {
		Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserId(
			String.valueOf(memberId));
		eventCaches.entrySet().stream()
			.filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
			.forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
	}

	private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
		try {
			emitter.send(SseEmitter.event()
				.name("sse")
				.id(eventId)
				.data(data));
		} catch (IOException exception) {
			emitterRepository.deleteById(emitterId);
		}
	}

	private String makeTimeIncludeId(Long memberId) {
		return memberId + "_" + System.currentTimeMillis();
	}

	@Transactional
	public void updateRead(long id) {
		Notification notification = notificationRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));
		notification.updateIsRead();
		log.info("읽었음으로 상태 변경" + notification.getIsRead().toString());
	}
}
