package com.beteam.willu.stomp.entity;

import java.util.ArrayList;
import java.util.List;

import com.beteam.willu.common.Timestamped;
import com.beteam.willu.post.entity.Post;
import com.beteam.willu.review.entity.Review;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "ChatRooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends Timestamped {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chatRoom_id")
	private Long id;

	@OneToOne
	@JoinColumn(name = "post_id")
	private Post post;

	// 채팅방 제목
	@Column(name = "chatTitle")
	private String chatTitle;

	// 상태
	@Column(name = "activated")
	private boolean activated;

	// 게시물 삭제시 해당 채팅방, 채팅메시지, 채팅유저 삭제
	@OneToMany(mappedBy = "chatRooms", cascade = CascadeType.ALL)
	@Builder.Default
	private List<UserChatRoom> userChatRoomList = new ArrayList<>();

	@OneToMany(mappedBy = "chatRooms", cascade = CascadeType.REMOVE)
	@Builder.Default
	private List<Chat> chatList = new ArrayList<>();

	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
	@Builder.Default
	private List<Review> reviewList = new ArrayList<>();

	public void updateActivated(boolean bool) {
		this.activated = bool;
	}
}
