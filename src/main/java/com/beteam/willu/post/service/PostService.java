package com.beteam.willu.post.service;

//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;

import java.util.List;

import com.beteam.willu.post.dto.PostRequestDto;
import com.beteam.willu.post.dto.PostResponseDto;
import com.beteam.willu.post.entity.Post;
import com.beteam.willu.user.entity.User;

public interface PostService {
	/**
	 *
	 * @param postRequestDto 작성할 게시글 정보
	 * @param user           게시글 작성 유저 정보
	 * @return 게시글 정보
	 */
	PostResponseDto createPost(PostRequestDto postRequestDto, User user);

	/*
	 *
	 * @param id    게시글 수정 아이디
	 * @param postRequestDto    게시글 수정 양식
	 * @param username  게시글 수정 할 유저
	 * @return
	 */
	PostResponseDto updatePost(Long id, PostRequestDto postRequestDto, String username);

	/**
	 * @param id    삭제할 게시글 아이디
	 * @param user  게시글 작성 유저
	 */
	void deletePost(Long id, User user);

	/**
	 *
	 * @param id  찾을 게시글 ID
	 * @return 찾은 게시글
	 */
	Post findPost(Long id);

	//    /**
	//     *
	//     * @param pageable
	//     * @return
	//     */
	//    Page<PostResponseDto> getPosts(Pageable pageable);
	List<PostResponseDto> getPosts();

	/**
	 *
	 * @param id    조회할 게시글 ID
	 * @return 해당 게시글 정보
	 */
	PostResponseDto getPost(Long id);

	void activateRecruitment(Long id, User user);

	void completeRecruitment(Long id, User user);
}