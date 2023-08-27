package com.beteam.willu.interest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beteam.willu.interest.entity.Interest;

public interface InterestRepository extends JpaRepository<Interest, Long> {
	boolean existsByReceiverIdAndSenderId(Long receiver, Long sender);

	Optional<Interest> findByReceiverIdAndSenderId(Long receiver, Long sender);
}
