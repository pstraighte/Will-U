package com.beteam.willu.stomp.dto;

import com.beteam.willu.stomp.entity.ChatRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomNameResponseDto {
    private String roomName;

    public ChatRoomNameResponseDto(ChatRoom chatRoom){
        System.out.println("chatRoom.get().getRoomName() = " + chatRoom.getRoomName());
        this.roomName = chatRoom.getRoomName();
    }
}
