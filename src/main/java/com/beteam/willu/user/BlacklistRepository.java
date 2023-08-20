package com.beteam.willu.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {
    boolean existsByReceiverAndSender(Long receiver, Long sender);

    Optional<Blacklist> findByReceiverAndSender(Long receiver, Long sender);
}
