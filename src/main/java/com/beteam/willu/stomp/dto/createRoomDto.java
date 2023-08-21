package com.beteam.willu.stomp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class createRoomDto {
    private String RoomName;

    public static createRoomDto create(){
        createRoomDto room = new createRoomDto();

        room.RoomName = UUID.randomUUID().toString();
        return room;
    }
}
