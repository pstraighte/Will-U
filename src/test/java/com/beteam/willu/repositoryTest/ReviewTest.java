package com.beteam.willu.repositoryTest;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.beteam.willu.post.dto.PostRequestDto;
import com.beteam.willu.post.entity.Post;
import com.beteam.willu.post.repository.PostRepository;
import com.beteam.willu.review.entity.Review;
import com.beteam.willu.review.repository.ReviewRepository;
import com.beteam.willu.stomp.entity.ChatRoom;
import com.beteam.willu.stomp.repository.ChatRoomRepository;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Review Test")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ReviewTest {
	@Autowired
	ReviewRepository reviewRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	ChatRoomRepository chatRoomRepository;
	@Autowired
	PostRepository postRepository;

	Post post1, post2;
	User receiver, sender;
	ChatRoom chatRoom1, chatRoom2;

	@BeforeAll
	void init() {
		sender = User.builder()
			.username("testUser")
			.password("password")
			.nickname("testUserNick")
			.email("testUser@email.com")
			.build();
		// 수신자
		receiver = User.builder()
			.username("testUser2")
			.password("password")
			.nickname("testUserNick2")
			.email("testUser2@email.com")
			.build();

		// 해당 채팅방 조회

		String title = "게시글 제목";
		String content = "게시글 내용";
		LocalDateTime promiseTime = LocalDateTime.now().plusDays(1);
		String area = "부산";
		Long maxNum = 5L;
		String category = "운동";

		PostRequestDto postRequestDto = new PostRequestDto(title, content, promiseTime, area, maxNum, category);
		post1 = new Post(postRequestDto);
		postRepository.save(post1);
		PostRequestDto postRequestDto2 = new PostRequestDto(title + 2, content + 2, promiseTime, area, maxNum,
			category);
		post2 = new Post(postRequestDto2);
		postRepository.save(post2);
		post1.setUser(receiver);
		post2.setUser(sender);
		chatRoom1 = ChatRoom.builder().chatTitle("채팅방 제목1").activated(true).post(post1).build();
		chatRoomRepository.save(chatRoom1);
		chatRoom2 = ChatRoom.builder().chatTitle("채팅방 제목2").activated(true).post(post2).build();
		chatRoomRepository.save(chatRoom2);
	}

	@DisplayName("리뷰 테스트: 등록")
	@Test
	@Order(1)
	public void createReview() {

		//리뷰 데이터 생성
		Review review = new Review(receiver, sender, chatRoom1, "리뷰 내용r->s", 4);
		// Review review2 = new Review(sender, receiver, chatRoom1, "리뷰 내용s-.r", 3);
		// Review review3 = new Review(receiver, sender, chatRoom1, "리뷰 내용r->s", 2);
		// Review review4 = new Review(sender, receiver, chatRoom1, "리뷰 내용s-.r", 1);

		//리뷰 데이터 저장
		reviewRepository.save(review);
		Assertions.assertEquals(review.getContent(), "리뷰 내용r->s");
	}
}
