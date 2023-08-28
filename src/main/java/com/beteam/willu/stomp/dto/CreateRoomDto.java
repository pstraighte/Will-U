package com.beteam.willu.stomp.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateRoomDto {
	private String roomName;

	public static CreateRoomDto create() {
		CreateRoomDto room = new CreateRoomDto();

		room.roomName = UUID.randomUUID().toString();
		return room;
	}
}
