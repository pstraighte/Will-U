package com.beteam.willu.review.service;

import com.beteam.willu.common.security.UserDetailsImpl;
import com.beteam.willu.review.dto.ReviewRequestDto;
import com.beteam.willu.review.dto.ReviewResponseDto;
import com.beteam.willu.review.entity.Review;
import com.beteam.willu.review.repository.ReviewRepository;
import com.beteam.willu.stomp.entity.ChatRoom;
import com.beteam.willu.stomp.repository.ChatRoomRepository;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j(topic = "reviewService")
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 리뷰 생성 (프론트 적용 완료)
    public void createReview(Long id, Long chatRoomId, ReviewRequestDto requestDto, UserDetailsImpl userDetails) {
        // 작성자
        User sender = userDetails.getUser();
        // 수신자
        User receiver = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        // 해당 채팅방 조회
        Optional<ChatRoom> ChatRoom = chatRoomRepository.findById(chatRoomId);

        //리뷰 데이터 생성
        Review review = new Review(receiver, sender, ChatRoom.get(), requestDto.getContent(), requestDto.getScore());

        //리뷰 데이터 저장
        reviewRepository.save(review);
    }

    // 리뷰 데이터 조회 (사용자 자신한테 저장된 휴기 조회)
    public ReviewResponseDto getReviews(Long id) {
        // 해당 사용자에게 작성된 리뷰 데이터 조회
        List<Review> reviewList = reviewRepository.findAllByReceiverId(id);

        return new ReviewResponseDto(reviewList);
    }

    // 특정 채팅방 리뷰 데이터 조회 (채팅방 확인용 - 유저가 이 채팅방에서 남긴 리뷰) (프론트 적용 완료)
    public ReviewResponseDto getChatRoomReviews(Long chatRoomId, UserDetailsImpl userDetails) {
        // 로그인한 유저 데이터 id
        Long user = userDetails.getUser().getId();

        // 해당 채팅방에서 로그인 유저가 남긴 채팅 데이터 조회
        List<Review> reviewList = reviewRepository.findAllBySenderIdAndChatRoomId(user, chatRoomId);

        return new ReviewResponseDto(reviewList);
    }

    // 리뷰 수정 (프론트 적용 완료)
    // 수정가능한 리뷰가 아닌지 확인(3일이 지난 리뷰인지 확인 필요)
    @Transactional
    public void updateReview(Long userId, Long chatRoomId, ReviewRequestDto requestDto, UserDetailsImpl userDetails) {
        // 로그인한 유저 데이터 id
        Long user = userDetails.getUser().getId();

        // 해당 채팅방에서 로그인 유저가 해당 유저에게 남긴 리뷰 조회
        Review review = reviewRepository.findBySenderIdAndChatRoomIdAndReceiverId(user, chatRoomId, userId);

        // 해당 리뷰의 작성자가 아닙니다. (프로필에서 수정할 때)
        if (!(user == review.getSender().getId())) {
            throw new IllegalArgumentException("리뷰 작성자가 아닙니다.");
        }

        // 해당 리뷰의 업데이트가 가능한 기한이 넘었는지 확인
//        review.getCreatedAt(); // 리뷰의 생성시간

        // 업데이트 내용
        String updateContent = requestDto.getContent();
        // 업데이트 점수
        long updateScore = requestDto.getScore();

        //리뷰 업데이트
        review.updateReview(updateContent, updateScore);
    }

    //리뷰 삭제 (프론트 적용 완료)
    public void deleteReview(Long userId, Long chatRoomId, UserDetailsImpl userDetails) {
        // 로그인한 유저 데이터 id
        Long user = userDetails.getUser().getId();

        // 해당 채팅방에서 로그인 유저가 해당 유저에게 남긴 리뷰 조회
        Review review = reviewRepository.findBySenderIdAndChatRoomIdAndReceiverId(user, chatRoomId, userId);

        // 해당 리뷰의 작성자가 아닙니다. (프로필에서 수정할 때)
        if (!(user == review.getSender().getId())) {
            throw new IllegalArgumentException("리뷰 작성자가 아닙니다.");
        }

        reviewRepository.delete(review);
    }
}
