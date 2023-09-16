package com.beteam.willu.stomp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beteam.willu.stomp.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	Optional<ChatRoom> findByPostId(Long id);

	Optional<ChatRoom> findChatRoomByPost_IdAndActivatedIsTrue(Long postId);

	Optional<ChatRoom> findChatRoomByPost_Id(Long postId);

	List<ChatRoom> findAllByActivated(boolean b);

}
