package com.beteam.willu.post.service;

import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beteam.willu.common.exception.RecruitmentStatusException;
import com.beteam.willu.hashtag.entity.BoardTagMap;
import com.beteam.willu.hashtag.entity.Tag;
import com.beteam.willu.hashtag.repository.BoardTagMapRepository;
import com.beteam.willu.hashtag.repository.HashTagRepository;
import com.beteam.willu.post.dto.PostRequestDto;
import com.beteam.willu.post.dto.PostResponseDto;
import com.beteam.willu.post.entity.Post;
import com.beteam.willu.post.querydsl.PostExpressions;
import com.beteam.willu.post.repository.PostRepository;
import com.beteam.willu.stomp.entity.ChatRoom;
import com.beteam.willu.stomp.entity.UserChatRoom;
import com.beteam.willu.stomp.repository.ChatRoomRepository;
import com.beteam.willu.stomp.repository.UserChatRoomsRepository;
import com.beteam.willu.user.entity.User;
import com.querydsl.core.types.Predicate;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

	private final PostRepository postRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final UserChatRoomsRepository userChatRoomsRepository;
	private final HashTagRepository hashTagRepository;
	private final BoardTagMapRepository boardTagMapRepository;

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

		for (String tag : postRequestDto.getTags()) {
			Tag newTag = hashTagRepository.findTagByContent(tag).orElse(null);
			if (Objects.isNull(newTag)) {
				//존재하지 않을 때
				newTag = new Tag(tag);
				hashTagRepository.save(newTag);
				// 태그와 게시글의 중간 테이블 생성
				BoardTagMap boardTagMap = new BoardTagMap(post, newTag);
				boardTagMapRepository.save(boardTagMap);

			} else {
				//존재할 때
				BoardTagMap boardTagMap = new BoardTagMap(post, newTag);
				boardTagMapRepository.save(boardTagMap);
			}
		}

		return new PostResponseDto(post);
	}

	// 게시글 전체 조회
	@Override
	@Transactional(readOnly = true)
	public Page<PostResponseDto> getPosts(Pageable pageable) {
		Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
		return posts.map(PostResponseDto::new);
	}

	// 게시글 상세 조회
	@Transactional
	public PostResponseDto getPost(Long id) {
		Post post = findPost(id);
		return new PostResponseDto(post);
	}

	@Transactional
	public void deleteOldTagMap(Long id) {
		boardTagMapRepository.deleteAllByPost_Id(id);
	}

	// 게시글 수정
	@Transactional
	@Override
	public PostResponseDto updatePost(Long id, PostRequestDto postRequestDto, String username) {
		Post post = findPost(id);
		if (!post.getUser().getUsername().equals(username)) {
			throw new RejectedExecutionException("작성자만 수정 가능합니다.");
		}

		String modifiedTitle = postRequestDto.getTitle();
		if (modifiedTitle != null) { //제목 수정되면 채팅방 이름 수정
			ChatRoom chatRoom = chatRoomRepository.findByPostId(id).orElseThrow();
			chatRoom.updateTitle(modifiedTitle);
		}
		// 기존 거 비우기
		//        boardTagMapRepository.deleteAllByPost_Id(id);
		// 입력한 태그 명 리스트 -> 이름으로 찾아서 존재하면
		for (String tag : postRequestDto.getTags()) {
			Tag newTag = hashTagRepository.findTagByContent(tag).orElse(null);
			if (Objects.isNull(newTag)) {
				//존재하지 않을 때
				newTag = new Tag(tag);
				hashTagRepository.save(newTag);
				// 태그와 게시글의 중간 테이블 생성
				BoardTagMap boardTagMap = new BoardTagMap(post, newTag);
				boardTagMapRepository.save(boardTagMap);

			} else {
				//존재할 때
				BoardTagMap boardTagMap = new BoardTagMap(post, newTag);
				boardTagMapRepository.save(boardTagMap);
			}
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
	public Page<PostResponseDto> searchPosts(String keyword, String criteria, boolean recruitment, Pageable pageable) {
		Predicate predicate = PostExpressions.createBooleanExpression(keyword, criteria, recruitment);
		Page<Post> postPage = postRepository.findAll(predicate, pageable);

		// Page<Post>를 Page<PostResponseDto>로 변환
		return postPage.map(PostResponseDto::new);
	}

	// 게시글 찾기
	@Override
	public Post findPost(Long id) {
		return postRepository.findById(id).orElseThrow(EntityNotFoundException::new);
	}
}
