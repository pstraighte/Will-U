package com.beteam.willu.serviceTest;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.beteam.willu.post.dto.PostRequestDto;
import com.beteam.willu.post.dto.PostResponseDto;
import com.beteam.willu.post.entity.Post;
import com.beteam.willu.post.repository.PostRepository;
import com.beteam.willu.post.service.PostServiceImpl;
import com.beteam.willu.stomp.entity.ChatRoom;
import com.beteam.willu.stomp.entity.UserChatRoom;
import com.beteam.willu.stomp.repository.ChatRoomRepository;
import com.beteam.willu.stomp.repository.UserChatRoomsRepository;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;

@DisplayName("POST SERVICE TEST")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
	@InjectMocks
	private PostServiceImpl postService;

	@Mock
	private PostRepository postRepository;

	@Mock
	private UserRepository userRepository;
	@Mock
	private ChatRoomRepository chatRoomRepository;
	@Mock
	private UserChatRoomsRepository userChatRoomsRepository;

	User user;

	@BeforeEach
	void init() {
		user = user();
		userRepository.save(user);
	}

	@Test
	public void testSearchPosts() {
		initPost();

		String keyword = "1";
		String criteria = "title";
		boolean recruitment = true;

		Pageable pageable = Mockito.mock(Pageable.class);

		//게시글 저장
		List<Post> searchedPosts = initPost().stream()
			.filter(post -> post.getTitle().contains(keyword))
			.filter(post -> post.getRecruitment() == Boolean.TRUE)
			.toList();
		when(postRepository.findByTitleContainingAndRecruitmentIsTrueOrderByCreatedAtDesc(keyword,
			pageable)).thenReturn(new PageImpl<>(searchedPosts, PageRequest.of(0, 5), searchedPosts.size()));

		Page<PostResponseDto> result = postService.searchPosts(keyword, criteria, recruitment, pageable);

		Assertions.assertThat(result).isNotNull();
		Assertions.assertThat(result.getTotalElements()).isEqualTo(2);
		Assertions.assertThat(result.stream().toList().get(0).getRecruitment()).isTrue();
	}

	@Test
	public void createPost() {

		PostRequestDto requestDto = PostRequestDto.builder()
			.title("게시글 제목13131")
			.content("게시글 내용13131")
			.promiseTime(LocalDateTime.now().plusDays(1))
			.promiseArea("진주")
			.maxnum(5L)
			.category("운동")
			.build();
		Post post = new Post(requestDto);
		post.setUser(user);
		when(postRepository.save(Mockito.any(Post.class))).thenReturn(post);

		ChatRoom chatRoom = ChatRoom.builder()
			.post(post)
			.chatTitle(post.getTitle())
			.activated(true)
			.build();

		when(chatRoomRepository.save(Mockito.any(ChatRoom.class))).thenReturn(chatRoom);

		UserChatRoom userChatRoom = UserChatRoom.builder().user(user).chatRooms(chatRoom).role("ADMIN").build();

		when(userChatRoomsRepository.save(Mockito.any(UserChatRoom.class))).thenReturn(userChatRoom);

		PostResponseDto savedPost = postService.createPost(requestDto, user);

		Assertions.assertThat(savedPost).isNotNull();
		Assertions.assertThat(savedPost.getTitle()).isEqualTo(requestDto.getTitle());

		verify(postRepository).save(any(Post.class));
		verify(chatRoomRepository).save(any(ChatRoom.class));
		verify(userChatRoomsRepository).save(any(UserChatRoom.class));
	}

	private List<Post> initPost() {
		User writer = user();
		List<Post> posts = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			Post post = Post.builder()
				.title("게시글 제목" + i)
				.content("게시글 내용" + i)
				.promiseTime(LocalDateTime.now().plusDays(1))
				.promiseArea("진주")
				.maxnum(3L)
				.category("운동")
				.user(writer)
				.build();
			posts.add(post);
		}
		return posts;
		// return postRepository.saveAll(posts);
	}

	@Test
	public void updatePost() {
		Long postId = 1L;
		String newTitle = "Updated Title";
		PostRequestDto postRequestDto = PostRequestDto.builder().title(newTitle).build();
		Post existingPost = post(user);
		ChatRoom chatRoom = ChatRoom.builder()
			.post(existingPost)
			.chatTitle(existingPost.getTitle())
			.activated(true)
			.build();
		existingPost.update(postRequestDto);
		when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
		when(chatRoomRepository.findByPostId(postId)).thenReturn(Optional.of(chatRoom));

		PostResponseDto result = postService.updatePost(postId, postRequestDto, user.getUsername());

		verify(postRepository).findById(postId);
		verify(chatRoomRepository).findByPostId(postId);

		Assertions.assertThat(result.getTitle()).isEqualTo(newTitle);
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
}

