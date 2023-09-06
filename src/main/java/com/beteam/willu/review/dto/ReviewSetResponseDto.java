package com.beteam.willu.review.dto;

import com.beteam.willu.review.entity.Review;

import lombok.Getter;

@Getter
public class ReviewSetResponseDto {
	private Long id; // 리뷰데이터 id
	private Long chatRoomId; // 채팅방 id (어떤 채팅방의 리뷰인지 알기위해)
	private Long receiverId; // 후기를받은 유저 id (어떤 채팅방의 리뷰인지 알기위해)
	private String senderNickname;
	private String content;
	private Double score;

	public ReviewSetResponseDto(Review review) {
		this.id = review.getId();
		this.chatRoomId = review.getChatRoom().getId();
		this.receiverId = review.getReceiver().getId();
		this.senderNickname = review.getSender().getNickname();
		this.content = review.getContent();
		this.score = review.getScore();
	}
}
