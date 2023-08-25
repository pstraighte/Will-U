package com.beteam.willu.user.review.dto;

import com.beteam.willu.user.review.entity.Review;
import lombok.Getter;

@Getter
public class ReviewSetResponseDto {
    private Long id;
    private String senderNickname;
    private String content;
    private long score;

    public ReviewSetResponseDto(Review review) {
        this.id = review.getId();
        this.senderNickname = review.getSender().getNickname();
        this.content = review.getContent();
        this.score = review.getScore();
    }
}
