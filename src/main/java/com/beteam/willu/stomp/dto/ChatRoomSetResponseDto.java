package com.beteam.willu.stomp.dto;

import com.beteam.willu.stomp.entity.UserChatRoom;
import lombok.Getter;

@Getter
public class ChatRoomSetResponseDto {
    private Long id;
    private Long userId; // 해당 유저의 id (후기 남기기 에 사용하기 위해서)
    private String role; // 해당 유저가 ADMIN인지 판단.
    private String chatName;
    private String userName;

    // 해당 dto 를 채팅방 불러오기랑 채팅방 유저 불러오기 둘다 사용중이라
    // 채팅방 id 와 사용자 id를 두개 불러온다.
    public ChatRoomSetResponseDto(UserChatRoom chatRoom) {
        this.id = chatRoom.getChatRooms().getId();
        this.userId = chatRoom.getUser().getId();
        this.role = chatRoom.getRole();
        this.chatName = chatRoom.getChatRooms().getChatTitle();
        this.userName = chatRoom.getUser().getUsername();
    }
}
