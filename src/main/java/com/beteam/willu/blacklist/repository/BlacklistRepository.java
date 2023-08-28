package com.beteam.willu.blacklist.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beteam.willu.blacklist.entity.Blacklist;

public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {
	boolean existsByReceiverIdAndSenderId(Long receiver, Long sender);

	Optional<Blacklist> findByReceiverIdAndSenderId(Long receiver, Long sender);
}
