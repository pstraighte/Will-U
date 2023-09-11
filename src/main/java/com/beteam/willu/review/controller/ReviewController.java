package com.beteam.willu.review.controller;

import com.beteam.willu.common.security.UserDetailsImpl;
import com.beteam.willu.review.dto.ReviewRequestDto;
import com.beteam.willu.review.dto.ReviewResponseDto;
import com.beteam.willu.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review API", description = "후기 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {
    private final ReviewService reviewService;

    // 리뷰 작성
    @Operation(summary = "사용자 후기 작성", description = "PathVariable형태의 사용자 id와, 채팅방 id, 정해진 파라미터를 받은 후 후기 데이터를 생성한다.")
    @Parameter(name = "content", required = true, schema = @Schema(type = "String"), description = "후기 내용")
    @Parameter(name = "score", required = true, schema = @Schema(type = "Integer"), description = "후기 점수")
    @PostMapping("/review/users/{userId}/chatRooms/{chatRoomId}")
    public void createReview(@PathVariable Long userId, @PathVariable Long chatRoomId,
                             @Valid @RequestBody ReviewRequestDto requestDto,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        reviewService.createReview(userId, chatRoomId, requestDto, userDetails);
        reviewService.updateUserScore(userId);
    }

    // 리뷰 데이터 조회 (프로필 용)
    @Operation(summary = "사용자 후기 조회", description = "PathVariable형태의 사용자 id를 이용해 사용자의 후기 데이터를 조회")
    @GetMapping("/review/users/{id}")
    public ReviewResponseDto getReviews(@PathVariable Long id) {
        return reviewService.getReviews(id);
    }

    // 특정 채팅방 리뷰 데이터 조회 (채팅방 확인용 - 유저가 이 채팅방에서 남긴 리뷰)
    @Operation(summary = "특정 채팅방 리뷰 데이터 조회", description = "PathVariable형태의 채팅방 id를 이용해 사용자의 후기 데이터를 조회" +
            "모집에 참여한 사용자 한명당 리뷰는 하나 이므로 해당 모집에서 사용자의 리뷰를 남겼는지 확인한다.")
    @GetMapping("/review/chatRooms/{chatRoomId}")
    public ReviewResponseDto getChatRoomReviews(@PathVariable Long chatRoomId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reviewService.getChatRoomReviews(chatRoomId, userDetails);
    }

    // 리뷰 수정 ()
    // 수정가능한 리뷰가 아닌지 확인(3일이 지난 리뷰인지 확인 필요) (미구현)
    @Operation(summary = "사용자 후기 수정", description = "PathVariable형태의 사용자 id와, 채팅방 id, 정해진 파라미터를 받은 후 후기 데이터를 수정한다.")
    @Parameter(name = "content", required = true, schema = @Schema(type = "String"), description = "후기 내용")
    @Parameter(name = "score", required = true, schema = @Schema(type = "Integer"), description = "후기 점수")
    @PutMapping("/review/users/{userId}/chatRooms/{chatRoomId}")
    public void updateReview(@PathVariable Long userId, @PathVariable Long chatRoomId,
                             @Valid @RequestBody ReviewRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        reviewService.updateReview(userId, chatRoomId, requestDto, userDetails);
        reviewService.updateUserScore(userId);
    }

    //리뷰 삭제
    @Operation(summary = "사용자 후기 삭제", description = "PathVariable형태의 사용자 id와, 채팅방 id를 이용해 해당하는 후기를 삭제한다.")
    @DeleteMapping("/review/users/{userId}/chatRooms/{chatRoomId}")
    public void deleteReview(@PathVariable Long userId, @PathVariable Long chatRoomId,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        reviewService.deleteReview(userId, chatRoomId, userDetails);
        reviewService.updateUserScore(userId);
    }
}
