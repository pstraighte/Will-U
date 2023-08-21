package com.beteam.willu.stomp.repository;

import com.beteam.willu.stomp.entity.UserChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChatRoomsRepository extends JpaRepository<UserChatRoom,Long> {
}
