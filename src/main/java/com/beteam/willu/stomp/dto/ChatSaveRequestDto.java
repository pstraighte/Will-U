package com.beteam.willu.stomp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatSaveRequestDto {
    private String userId;
    private String chatContent;
    private Long roomId;
    private LocalDateTime createdAt;
}

