package com.beteam.willu.stomp.repository;

import com.beteam.willu.stomp.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByPostId(Long id);

    Optional<ChatRoom> findChatRoomByPost_IdAndActivatedIsTrue(Long postId);

    List<ChatRoom> findAllByActivated(boolean b);

}
