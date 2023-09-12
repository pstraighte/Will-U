package com.beteam.willu.stomp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatSaveRequestDto {
    private String userId;
    @NotBlank(message = "채팅내용을 입력하세요")
    private String chatContent;
    private Long roomId;
    private LocalDateTime createdAt;
}

