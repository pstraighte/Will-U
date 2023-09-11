package com.beteam.willu.repositoryTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.beteam.willu.post.dto.PostRequestDto;
import com.beteam.willu.post.entity.Post;
import com.beteam.willu.post.repository.PostRepository;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("postRepository Test")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class PostRepositoryTest {
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserRepository userRepository;

	Post post;
	User user;

	@DisplayName("게시글 단건 조회 테스트")
	@Test
	@Order(1)
	public void getOnePost() {
		post = Post.builder()
			.title("게시글 제목13131")
			.content("게시글 내용13131")
			.promiseTime(LocalDateTime.now().plusDays(1))
			.promiseArea("진주")
			.maxnum(5L)
			.category("운동")
			.user(user)
			.build();
		postRepository.save(post);
		Post savedPost = postRepository.findById(post.getId()).get();

		Assertions.assertThat(savedPost.getTitle()).isEqualTo("게시글 제목13131");
	}

	@DisplayName("게시글 페이징 조회 테스트")
	@Test
	@Order(2)
	public void allByCreatedDateDesc() {
		Pageable pageable = PageRequest.of(1, 5);
		initPost();
		Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
		List<String> getPagingPostsTitle = new ArrayList<>();
		for (Post post : posts) {
			getPagingPostsTitle.add(post.getTitle());
			System.out.println("post.getTitle():" + post.getTitle());
		}
		// posts.stream().forEach((post) -> {
		// 	getPagingPostsTitle.add(post.getTitle());
		// 	System.out.println("post.getTitle() = " + post.getTitle());
		// });
		int i = 16;
		for (String title : getPagingPostsTitle) {
			Assertions.assertThat(title).isEqualTo("게시글 제목" + i--);
		}
	}

	@DisplayName("게시글 수정 테스트")
	@Test
	@Order(3)
	public void updatePost() {
		user = user();
		post = Post.builder()
			.title("게시글 제목13131")
			.content("게시글 내용13131")
			.promiseTime(LocalDateTime.now().plusDays(1))
			.promiseArea("진주")
			.maxnum(5L)
			.category("운동")
			.user(user)
			.build();
		//when
		String title = "게시글 제목-수정";
		String content = "게시글 내용-수정";
		LocalDateTime promiseTime = LocalDateTime.now().plusDays(1);
		String area = "대구";
		Long maxNum = 5L;
		String category = "취미";

		PostRequestDto postRequestDto = new PostRequestDto(title, content, promiseTime, area, maxNum, category);

		post.update(postRequestDto);
		//then
		Assertions.assertThat(post.getTitle()).isSameAs("게시글 제목-수정");
	}

	private User user() {
		return User.builder()
			.username("testUser")
			.password("password")
			.nickname("testUserNick")
			.email("testUser@email.com")
			.build();
	}

	private List<Post> initPost() {
		User writer = user();
		userRepository.save(writer);
		List<Post> posts = new ArrayList<>();
		for (int i = 11; i <= 21; i++) {
			post = Post.builder()
				.title("게시글 제목" + i)
				.content("게시글 내용" + i)
				.promiseTime(LocalDateTime.now().plusDays(1))
				.promiseArea("진주")
				.maxnum(5L)
				.category("운동")
				.user(writer)
				.build();
			posts.add(post);
		}

		return postRepository.saveAll(posts);
	}
}
