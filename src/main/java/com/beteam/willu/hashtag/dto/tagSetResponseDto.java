package com.beteam.willu.hashtag.dto;

import com.beteam.willu.hashtag.entity.BoardTagMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class tagSetResponseDto {
    private Long id;
    private String tag;

    public tagSetResponseDto(BoardTagMap boardTagMap) {
        this.id = boardTagMap.getTag().getId();
        this.tag = boardTagMap.getTag().getContent();
    }
}
