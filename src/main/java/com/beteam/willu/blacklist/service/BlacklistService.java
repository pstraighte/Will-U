package com.beteam.willu.blacklist.service;

import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beteam.willu.blacklist.entity.Blacklist;
import com.beteam.willu.blacklist.repository.BlacklistRepository;
import com.beteam.willu.notification.dto.NotificationEvent;
import com.beteam.willu.notification.entity.NotificationType;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;
import com.sun.jdi.request.DuplicateRequestException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "blacklistService")
@Service
@RequiredArgsConstructor
public class BlacklistService {
	private final UserRepository userRepository;
	private final BlacklistRepository blacklistRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional //차단 유저 추가
	public void addBlacklist(Long id, User user) {
		User receiver = userRepository.findById(id).orElseThrow();

		if (blacklistRepository.existsByReceiverIdAndSenderId(receiver.getId(), user.getId())) {
			throw new DuplicateRequestException("이미 차단된 유저 입니다.");
		} else {
			Blacklist blacklist = new Blacklist(receiver, user);
			blacklistRepository.save(blacklist);
		}
		//sse 테스트용
		NotificationEvent event = NotificationEvent.builder()
			.title("차단 유저 추가")
			.notificationType(NotificationType.LOGIN_DONE)
			.receiver(receiver)
			.publisher(user)
			.content(receiver.getNickname() + "님을 차단했습니다")
			.build();
		eventPublisher.publishEvent(event);
	}

	@Transactional  //차단 유저 해제
	public void removeBlacklist(Long id, User user) {
		User receiver = userRepository.findById(id).orElseThrow();

		Optional<Blacklist> blacklistOptional = blacklistRepository.findByReceiverIdAndSenderId(receiver.getId(),
			user.getId());
		if (blacklistOptional.isPresent()) {
			blacklistRepository.delete(blacklistOptional.get());
		} else {
			throw new IllegalArgumentException("이미 차단해제 된 유저 입니다.");
		}
	}
}
