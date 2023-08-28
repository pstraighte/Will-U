package com.beteam.willu.stomp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beteam.willu.stomp.entity.ChatRoom;
import com.beteam.willu.stomp.entity.UserChatRoom;
import com.beteam.willu.user.entity.User;

public interface UserChatRoomsRepository extends JpaRepository<UserChatRoom, Long> {
	List<UserChatRoom> findAllByUserId(Long id);

	Optional<UserChatRoom> findByChatRoomsIdAndUserId(Long chatid, Long userId);

	List<UserChatRoom> findAllByChatRoomsId(Long chatRoomId);

	boolean existsByUserAndChatRooms(User user, ChatRoom chatRoom);
}
