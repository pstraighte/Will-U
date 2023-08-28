package com.beteam.willu.review.dto;

import java.util.ArrayList;
import java.util.List;

import com.beteam.willu.review.entity.Review;

import lombok.Getter;
import lombok.Setter;

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
