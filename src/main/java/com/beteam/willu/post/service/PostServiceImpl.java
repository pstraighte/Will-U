package com.beteam.willu.post.service;

import java.util.concurrent.RejectedExecutionException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beteam.willu.common.exception.RecruitmentStatusException;
import com.beteam.willu.post.dto.PostRequestDto;
import com.beteam.willu.post.dto.PostResponseDto;
import com.beteam.willu.post.entity.Post;
import com.beteam.willu.post.repository.PostRepository;
import com.beteam.willu.stomp.entity.ChatRoom;
import com.beteam.willu.stomp.entity.UserChatRoom;
import com.beteam.willu.stomp.repository.ChatRoomRepository;
import com.beteam.willu.stomp.repository.UserChatRoomsRepository;
import com.beteam.willu.user.entity.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

	private final PostRepository postRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final UserChatRoomsRepository userChatRoomsRepository;

	// 게시글 작성
	@Override
	@Transactional
	public PostResponseDto createPost(PostRequestDto postRequestDto, User user) {
		Post post = new Post(postRequestDto);
		post.setUser(user);
		postRepository.save(post);

		// 게시글 생성 과 함께 채팅방 개설
		ChatRoom chatRoom = ChatRoom.builder()
			.post(post)
			.chatTitle(post.getTitle())
			.activated(true)
			.build();

		chatRoomRepository.save(chatRoom);

		// UserChatRooms 생성

		UserChatRoom userChatRoom = UserChatRoom.builder().user(user).chatRooms(chatRoom).role("ADMIN").build();

		userChatRoomsRepository.save(userChatRoom);

		return new PostResponseDto(post);
	}

	// 게시글 전체 조회
	@Override
	@Transactional(readOnly = true)
	public Page<PostResponseDto> getPosts(Pageable pageable) {
		Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);

		//        Page<Post> posts = postRepository.findAll(pageable);
		return posts.map(PostResponseDto::new);
	}
	// @Override
	// public List<PostResponseDto> getPosts() {
	// 	return postRepository.findAllByOrderByCreatedAtDesc()
	// 		.stream()
	// 		.map(PostResponseDto::new)
	// 		.toList();
	// }
	//    @Override
	//    public Page<PostResponseDto> getPosts(Pageable pageable) {
	//        Page<Post> posts = postRepository.findAll(pageable);
	//        return posts.map(post -> new PostResponseDto(post));
	//    }

	// 게시글 상세 조회

	public PostResponseDto getPost(Long id) {
		Post post = findPost(id);
		return new PostResponseDto(post);
	}

	// 게시글 수정
	@Transactional
	@Override
	public PostResponseDto updatePost(Long id, PostRequestDto postRequestDto, String username) {
		Post post = findPost(id);
		if (!post.getUser().getUsername().equals(username)) {
			throw new RejectedExecutionException("작성자만 수정 가능합니다.");
		}
		post.update(postRequestDto);
		return new PostResponseDto(post);
	}

	// 게시글 삭제
	@Override
	public void deletePost(Long id, User user) {
		Post post = findPost(id);
		if (!post.getUser().getId().equals(user.getId())) {
			throw new RejectedExecutionException("작성자만 삭제 가능합니다.");
		}
		postRepository.delete(post);
	}

	// 모집완료 -> 모집중으로 변경
	@Override
	@Transactional
	public void activateRecruitment(Long id, User user) {
		Post post = findPost(id);

		if (!post.getUser().getId().equals(user.getId())) {
			throw new RejectedExecutionException("작성자만 변경 가능합니다.");
		}

		if (post.getRecruitment()) {
			throw new RecruitmentStatusException();
		} else {
			post.setRecruitment(true); // 저장 안 됨
			System.out.println(post.getRecruitment());
		}
	}

	// 모집중 -> 모집완료로 변경
	@Override
	@Transactional
	public void completeRecruitment(Long id, User user) {
		Post post = findPost(id);

		if (!post.getUser().getId().equals(user.getId())) {
			throw new RejectedExecutionException("작성자만 변경 가능합니다.");
		}

		if (post.getRecruitment()) {
			post.setRecruitment(false); // 저장 안 됨
		} else {
			throw new RecruitmentStatusException();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PostResponseDto> searchPosts(String keyword, String criteria, boolean recruitment,
		Pageable pageable) {
		Page<Post> searchResultPage;
		if (recruitment) {
			searchResultPage = switch (criteria) {
				case "username" -> postRepository.findByUser_UsernameContainingAndRecruitmentIsTrue(keyword, pageable);
				case "content" -> postRepository.findByContentContainingAndRecruitmentIsTrue(keyword, pageable);
				case "category" -> postRepository.findByCategoryContainingAndRecruitmentIsTrue(keyword, pageable);
				default -> postRepository.findByTitleContainingAndRecruitmentIsTrue(keyword, pageable);
			};
		} else {
			searchResultPage = switch (criteria) {
				case "username" -> postRepository.findByUser_UsernameContaining(keyword, pageable);
				case "content" -> postRepository.findByContentContaining(keyword, pageable);
				case "category" -> postRepository.findByCategoryContaining(keyword, pageable);
				default -> postRepository.findByTitleContaining(keyword, pageable);
			};
		}

		return searchResultPage.map(PostResponseDto::new);
	}

	// 게시글 찾기
	@Override
	public Post findPost(Long id) {
		return postRepository.findById(id).orElseThrow(EntityNotFoundException::new);
	}
}
