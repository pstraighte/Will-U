package com.beteam.willu.user.review.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewRequestDto {
    private String content;
    private long score;
}
