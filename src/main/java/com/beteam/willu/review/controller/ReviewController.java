package com.beteam.willu.review.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beteam.willu.common.security.UserDetailsImpl;
import com.beteam.willu.review.dto.ReviewRequestDto;
import com.beteam.willu.review.dto.ReviewResponseDto;
import com.beteam.willu.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {
	private final ReviewService reviewService;

	// 리뷰 작성
	@PostMapping("/review/users/{id}")
	public void createReview(@PathVariable Long id, @RequestBody ReviewRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		reviewService.createReview(id, requestDto, userDetails);
	}

	// 리뷰 데이터 조회
	@GetMapping("/review/users/{id}")
	public ReviewResponseDto getReviews(@PathVariable Long id) {
		return reviewService.getReviews(id);
	}
}
