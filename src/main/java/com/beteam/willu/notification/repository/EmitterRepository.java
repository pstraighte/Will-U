package com.beteam.willu.notification.repository;

import java.util.Map;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {
	SseEmitter save(String emitterId, SseEmitter sseEmitter);

	void saveEventCache(String emitterId, Object event);

	Map<String, SseEmitter> findAllEmitterStartWithById(String userId);

	Map<String, Object> findAllEventCacheStartWithById(String userId);

	void deleteById(String id);

	void deleteAllEmitterStartWithId(String userId);

	void deleteAllEventCacheStartWithId(String userId);
}
