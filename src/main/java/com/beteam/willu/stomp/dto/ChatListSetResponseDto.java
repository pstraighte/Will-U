package com.beteam.willu.stomp.dto;

import com.beteam.willu.stomp.entity.Chat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatListSetResponseDto {
    private Long id;
    private String userName;
    private String chatContent;
    private LocalDateTime createAt;

    public ChatListSetResponseDto(Chat chat) {
        this.id = chat.getId();
        this.userName = chat.getUser().getUsername();
        this.chatContent = chat.getChatContent();
        this.createAt = chat.getCreatedAt();
    }
}
