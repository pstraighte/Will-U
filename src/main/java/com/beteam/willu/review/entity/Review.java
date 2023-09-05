package com.beteam.willu.review.entity;

import org.springframework.transaction.annotation.Transactional;

import com.beteam.willu.common.Timestamped;
import com.beteam.willu.stomp.entity.ChatRoom;
import com.beteam.willu.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "reviews")
@NoArgsConstructor
public class Review extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "receiver_id") //리뷰 수신자-admin
	private User receiver;

	@ManyToOne
	@JoinColumn(name = "sender_id") //리뷰 작성자-게스트
	private User sender;

	@ManyToOne
	@JoinColumn(name = "chatRoom_id")
	private ChatRoom chatRoom;

	private String content;

	private Double score;

	public Review(User receiver, User sender, ChatRoom userChatRoom, String content, Double score) {
		this.receiver = receiver;
		this.sender = sender;
		this.chatRoom = userChatRoom;
		this.content = content;
		this.score = score;
	}

	@Transactional
	public void updateReview(String content, Double score) {
		this.content = content;
		this.score = score;
	}
}
