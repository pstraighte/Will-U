package com.beteam.willu.stomp.repository;

import com.beteam.willu.stomp.entity.UserChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserChatRoomsRepository extends JpaRepository<UserChatRoom,Long> {
    List<UserChatRoom> findByUserId(Long id);
}
