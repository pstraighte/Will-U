package com.beteam.willu.stomp.repository;

import com.beteam.willu.stomp.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,Long> {
    List<Chat> findAllByChatRoomsId(Long id);
}
