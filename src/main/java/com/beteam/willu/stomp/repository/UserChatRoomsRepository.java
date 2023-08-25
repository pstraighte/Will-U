package com.beteam.willu.stomp.repository;

import com.beteam.willu.stomp.entity.ChatRoom;
import com.beteam.willu.stomp.entity.UserChatRoom;
import com.beteam.willu.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserChatRoomsRepository extends JpaRepository<UserChatRoom, Long> {
    List<UserChatRoom> findAllByUserId(Long id);

    Optional<UserChatRoom> findByChatRoomsIdAndUserId(Long chatid, Long userId);

    List<UserChatRoom> findAllByChatRoomsId(Long chatRoomId);

    boolean existsByUserAndChatRooms(User user, ChatRoom chatRoom);
}
