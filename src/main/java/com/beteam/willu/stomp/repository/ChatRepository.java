package com.beteam.willu.stomp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beteam.willu.stomp.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
	List<Chat> findAllByChatRoomsId(Long id);
}
