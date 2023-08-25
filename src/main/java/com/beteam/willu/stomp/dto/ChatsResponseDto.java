package com.beteam.willu.stomp.dto;


import com.beteam.willu.stomp.entity.Chat;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChatsResponseDto {
    private List<ChatListSetResponseDto> chatContentList = new ArrayList<>();

    public ChatsResponseDto(List<Chat> chatList) {
        setChatList(chatList);
    }

    public void setChatList(List<Chat> chatList) {
        
        for (Chat chat : chatList) {
            chatContentList.add(new ChatListSetResponseDto(chat));
        }
    }
}
