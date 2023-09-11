package com.beteam.willu.serviceTest;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.beteam.willu.post.entity.Post;
import com.beteam.willu.stomp.dto.ChatRoomsResponseDto;
import com.beteam.willu.stomp.entity.ChatRoom;
import com.beteam.willu.stomp.entity.UserChatRoom;
import com.beteam.willu.stomp.repository.UserChatRoomsRepository;
import com.beteam.willu.stomp.service.ChatRoomService;
import com.beteam.willu.user.entity.User;

@DisplayName("CHAT SERVICE TEST")
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ChatServiceTest {
	@InjectMocks
	private ChatRoomService chatRoomService;

	@Mock
	private UserChatRoomsRepository userChatRoomsRepository;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@Disabled
	public void getChatRoom() {
		User user = user();
		Post post = post(user);
		ChatRoom chatRoom = chatRoom(post);
		UserChatRoom userChatRoom1 = UserChatRoom.builder().user(user).chatRooms(chatRoom).role("ADMIN").build();

		User user2 = User.builder().id(2L).build();
		UserChatRoom userChatRoom2 = UserChatRoom.builder().user(user2).chatRooms(chatRoom).role("USER").build();

		List<UserChatRoom> userChatRooms = new ArrayList<>();
		userChatRooms.add(userChatRoom1);
		userChatRooms.add(userChatRoom2);

		when(userChatRoomsRepository.findAllByUserId(1L)).thenReturn(userChatRooms);

		ChatRoomsResponseDto response = chatRoomService.getRooms(1L);

		Assertions.assertThat(response).isEqualTo(2);

	}

	private User user() {
		return User.builder()
			.username("testUser")
			.password("password")
			.nickname("testUserNick")
			.email("testUser@email.com")
			.build();
	}

	private Post post(User user) {
		return Post.builder()
			.title("게시글 제목13131")
			.content("게시글 내용13131")
			.promiseTime(LocalDateTime.now().plusDays(1))
			.promiseArea("진주")
			.maxnum(5L)
			.category("운동")
			.user(user)
			.build();
	}

	private ChatRoom chatRoom(Post post) {
		return ChatRoom.builder()
			.post(post)
			.chatTitle("채팅방 제목")
			.activated(true)
			.build();
	}
}
