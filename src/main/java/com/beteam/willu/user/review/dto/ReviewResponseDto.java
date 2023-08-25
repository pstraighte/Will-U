package com.beteam.willu.user.review.dto;

import com.beteam.willu.user.review.entity.Review;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class ReviewResponseDto {

    private List<ReviewSetResponseDto> reviews = new ArrayList<>();

    public ReviewResponseDto(List<Review> reviewList) {
        setReviewList(reviewList);
    }

    public void setReviewList(List<Review> reviewList) {
        for (Review review : reviewList) {
            reviews.add(new ReviewSetResponseDto(review));
        }
    }

}
