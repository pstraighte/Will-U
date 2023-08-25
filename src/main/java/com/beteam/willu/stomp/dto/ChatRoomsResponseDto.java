package com.beteam.willu.stomp.dto;

import com.beteam.willu.stomp.entity.UserChatRoom;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChatRoomsResponseDto {
    private List<ChatRoomSetResponseDto> chatRoomList = new ArrayList<>();

    public ChatRoomsResponseDto(List<UserChatRoom> chatRooms) {
        setChatRoomList(chatRooms);
    }

    public void setChatRoomList(List<UserChatRoom> chatRooms) {
        for (UserChatRoom chatRoom : chatRooms) {
            chatRoomList.add(new ChatRoomSetResponseDto(chatRoom));
        }
    }
}
