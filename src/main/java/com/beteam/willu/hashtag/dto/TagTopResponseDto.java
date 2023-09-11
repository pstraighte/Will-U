package com.beteam.willu.hashtag.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagTopResponseDto {
    private String tagName;
    private Long count;

    public TagTopResponseDto(String tagName, Long count) {
        this.tagName = tagName;
        this.count = count;
    }
}
