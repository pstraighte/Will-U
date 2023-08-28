package com.beteam.willu.stomp.dto;

import com.beteam.willu.stomp.entity.ChatRoom;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomNameResponseDto {
	private String roomName;

	public ChatRoomNameResponseDto(ChatRoom chatRoom) {
		this.roomName = chatRoom.getChatTitle();
	}
}
