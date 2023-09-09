package com.beteam.willu.repositoryTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.beteam.willu.post.entity.Post;
import com.beteam.willu.post.repository.PostRepository;
import com.beteam.willu.stomp.entity.Chat;
import com.beteam.willu.stomp.entity.ChatRoom;
import com.beteam.willu.stomp.entity.UserChatRoom;
import com.beteam.willu.stomp.repository.ChatRepository;
import com.beteam.willu.stomp.repository.ChatRoomRepository;
import com.beteam.willu.stomp.repository.UserChatRoomsRepository;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Chat 관련 repository Tests")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class ChatTest {
	@Autowired
	private ChatRoomRepository chatRoomRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserChatRoomsRepository userChatRoomsRepository;
	@Autowired
	private ChatRepository chatRepository;

	private User user;
	private Post post;
	private ChatRoom chatRoom;
	private UserChatRoom userChatRoom;

	@BeforeEach
	void setUp() {
		user = createUser();
		post = createPost();
		chatRoom = createChatRoom();
		userChatRoom = createUserChatRoom();
	}

	@DisplayName("chatRoom 테스트: postId로 조회")
	@Test
	@Order(1)
	void findByPostId() {
		ChatRoom foundChatRoom = chatRoomRepository.findByPostId(post.getId())
			.orElseThrow(() -> new IllegalArgumentException("chatRoom 없음"));
		System.out.println("foundChatRoom = " + foundChatRoom.toString());
		Assertions.assertThat(foundChatRoom.getChatTitle()).isEqualTo("채팅방 제목");
	}

	@DisplayName("chatRoom 테스트: 활성화된 chatRoom postId로 조회")
	@Test
	@Order(2)
	void findChatRoomByPost_IdAndActivatedIsTrue() {
		ChatRoom foundChatRoom = chatRoomRepository.findChatRoomByPost_IdAndActivatedIsTrue(post.getId()).orElseThrow();
		System.out.println("foundChatRoom = " + foundChatRoom.toString());
		Assertions.assertThat(foundChatRoom.getChatTitle()).isEqualTo("채팅방 제목");
	}

	@DisplayName("chatRoom 테스트: 삭제")
	@Test
	@Order(6)
	void deleteChatRoom() {
		chatRoomRepository.delete(chatRoom);
		Assertions.assertThat(chatRoomRepository.findById(1L).isPresent()).isFalse();
	}

	@DisplayName("userChatRoom 테스트: UserId로 userChatRoom 조회")
	@Test
	@Order(3)
	void getUserChatRooms() {
		List<UserChatRoom> userChatRooms = userChatRoomsRepository.findAllByUserId(user.getId());
		for (UserChatRoom ucr : userChatRooms) {
			System.out.println("ucr.getId() = " + ucr.getId());
		}
		Assertions.assertThat(userChatRooms.size()).isNotEqualTo(0);
		System.out.println("chatRoomSize" + userChatRooms.size());
	}

	@DisplayName("userChatRoom 테스트: ChatRoomId와 UserId로 조회 userChatRoom 조회")
	@Test
	@Order(4)
	void getUserChatRoomsByChatRoomIdAndUserId() {

		Assertions.assertThat(userChatRoom.getUser().getId()).isEqualTo(4L);
		Assertions.assertThat(userChatRoom.getChatRooms().getId()).isEqualTo(4L);
	}

	@DisplayName("chat 테스트: 등록 && ChatRoomId로 조회")
	@Test
	@Order(5)
	void getChatByChatRoomId() {

		Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(user.getId());

		if (optionalChatRoom.isPresent()) {

			ChatRoom chatRoom = optionalChatRoom.get();

			for (int i = 0; i < 3; i++) {
				Chat chat = createChat(user, chatRoom, "하이용" + i);
				chatRepository.save(chat);
			}
		}

		List<Chat> chatList = chatRepository.findAllByChatRoomsId(optionalChatRoom.get().getId());
		System.out.println("optionalChatRoomId: " + optionalChatRoom.get().getId());
		Assertions.assertThat(chatList.size()).isEqualTo(3);
	}

	private User createUser() {
		return userRepository.save(User.builder()
			.username("testUser")
			.password("password")
			.nickname("testUserNick")
			.email("testUser@email.com")
			.build());
	}

	private Post createPost() {
		return postRepository.save(Post.builder()
			.title("게시글 제목")
			.content("게시글 내용")
			.promiseTime(LocalDateTime.now().plusDays(1))
			.promiseArea("부산")
			.maxnum(5L)
			.category("운동")
			.user(user)
			.build());
	}

	private ChatRoom createChatRoom() {
		return chatRoomRepository.save(ChatRoom.builder()
			.chatTitle("채팅방 제목")
			.activated(true)
			.post(post)
			.build());
	}

	private UserChatRoom createUserChatRoom() {
		return userChatRoomsRepository.save(UserChatRoom.builder()
			.user(user)
			.role("ADMIN")
			.chatRooms(chatRoom)
			.build());
	}

	private Chat createChat(User user, ChatRoom chatRoom, String content) {
		return chatRepository.save(Chat.builder()
			.user(user)
			.chatRooms(chatRoom)
			.chatContent(content)
			.createdAt(LocalDateTime.now())
			.build());
	}
}

