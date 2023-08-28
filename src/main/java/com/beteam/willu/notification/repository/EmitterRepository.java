package com.beteam.willu.notification.repository;

import java.util.Map;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {
	SseEmitter save(String emitterId, SseEmitter sseEmitter);

	void saveEventCache(String emitterId, Object event);

	Map<String, SseEmitter> findAllEmitterStartWithByUserId(String memberId);

	Map<String, Object> findAllEventCacheStartWithByUserId(String memberId);

	void deleteById(String id);

	void deleteAllEmitterStartWithId(String memberId);

	void deleteAllEventCacheStartWithId(String memberId);
}
