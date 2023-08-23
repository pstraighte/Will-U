package com.beteam.willu.stomp.dto;

import com.beteam.willu.stomp.entity.UserChatRoom;
import lombok.Getter;

@Getter
public class ChatRoomSetResponseDto {
    private Long id;
    private String chatName;

    public ChatRoomSetResponseDto(UserChatRoom chatRoom) {
        this.id = chatRoom.getChatRooms().getId();
        this.chatName = chatRoom.getChatRooms().getChatTitle();
    }
}
