package com.beteam.willu.review.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.beteam.willu.common.security.UserDetailsImpl;
import com.beteam.willu.review.dto.ReviewRequestDto;
import com.beteam.willu.review.dto.ReviewResponseDto;
import com.beteam.willu.review.entity.Review;
import com.beteam.willu.review.repository.ReviewRepository;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "reviewService")
@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;

	public void createReview(Long id, ReviewRequestDto requestDto, UserDetailsImpl userDetails) {
		// 작성자
		User sender = userDetails.getUser();
		// 수신자
		User receiver = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

		//리뷰 데이터 생성
		Review review = new Review(receiver, sender, requestDto.getContent(), requestDto.getScore());

		//리뷰 데이터 저장
		reviewRepository.save(review);
	}

	// 리뷰 데이터 조회 (사용자 자신한테 저장된 휴기 조회)
	public ReviewResponseDto getReviews(Long id) {
		// 해당 사용자에게 작성된 리뷰 데이터 조회
		List<Review> reviewList = reviewRepository.findAllByReceiverId(id);

		return new ReviewResponseDto(reviewList);
	}
}
