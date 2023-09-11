package com.beteam.willu.review.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewRequestDto {
    @NotBlank(message = "빈칸없이 입력")
    private String content;
    private Integer score;
}