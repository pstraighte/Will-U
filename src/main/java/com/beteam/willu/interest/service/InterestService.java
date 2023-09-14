package com.beteam.willu.interest.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beteam.willu.interest.entity.Interest;
import com.beteam.willu.interest.repository.InterestRepository;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;
import com.sun.jdi.request.DuplicateRequestException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "InterestService")
@Service
@RequiredArgsConstructor
public class InterestService {
	private final UserRepository userRepository;
	private final InterestRepository interestRepository;

	@Transactional  //관심유저 추가
	public void addInterest(Long id, User user) {
		User receiver = userRepository.findById(id).orElseThrow();

		if (interestRepository.existsByReceiverIdAndSenderId(receiver.getId(), user.getId())) {
			throw new DuplicateRequestException("이미 관심등록 된 유저 입니다.");
		} else if (receiver.getId().equals(user.getId())) {
			throw new IllegalArgumentException("자신에게 관심등록을 할 수 없습니다.");
		} else {
			Interest interest = new Interest(receiver, user);
			interestRepository.save(interest);
		}
	}

	@Transactional
	public void removeInterest(Long id, User user) {
		User receiver = userRepository.findById(id).orElseThrow();

		Optional<Interest> interestOptional = interestRepository.findByReceiverIdAndSenderId(receiver.getId(),
			user.getId());
		if (interestOptional.isPresent()) {
			interestRepository.delete(interestOptional.get());
		} else {
			throw new IllegalArgumentException("이미 관심해제 된 유저 입니다.");
		}
	}
}
