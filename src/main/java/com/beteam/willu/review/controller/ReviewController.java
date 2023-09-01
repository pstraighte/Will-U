package com.beteam.willu.review.controller;

import com.beteam.willu.common.security.UserDetailsImpl;
import com.beteam.willu.review.dto.ReviewRequestDto;
import com.beteam.willu.review.dto.ReviewResponseDto;
import com.beteam.willu.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {
    private final ReviewService reviewService;

    // 리뷰 작성
    @PostMapping("/review/users/{userId}/chatRooms/{chatRoomId}")
    public void createReview(@PathVariable Long userId, @PathVariable Long chatRoomId, @RequestBody ReviewRequestDto requestDto,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        reviewService.createReview(userId, chatRoomId, requestDto, userDetails);
    }

    // 리뷰 데이터 조회 (프로필 용)
    @GetMapping("/review/users/{id}")
    public ReviewResponseDto getReviews(@PathVariable Long id) {
        return reviewService.getReviews(id);
    }

    // 특정 채팅방 리뷰 데이터 조회 (채팅방 확인용 - 유저가 이 채팅방에서 남긴 리뷰)
    @GetMapping("/review/chatRooms/{chatRoomId}")
    public ReviewResponseDto getChatRoomReviews(@PathVariable Long chatRoomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reviewService.getChatRoomReviews(chatRoomId, userDetails);
    }

    // 리뷰 수정 ()
    // 수정가능한 리뷰가 아닌지 확인(3일이 지난 리뷰인지 확인 필요) (미구현)
    @PutMapping("/review/users/{userId}/chatRooms/{chatRoomId}")
    public void updateReview(@PathVariable Long userId, @PathVariable Long chatRoomId, @RequestBody ReviewRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        reviewService.updateReview(userId, chatRoomId, requestDto, userDetails);
    }

    //리뷰 삭제
    @DeleteMapping("/review/users/{userId}/chatRooms/{chatRoomId}")
    public void deleteReview(@PathVariable Long userId, @PathVariable Long chatRoomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        reviewService.deleteReview(userId, chatRoomId, userDetails);
    }

}
