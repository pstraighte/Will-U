package com.beteam.willu.post;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beteam.willu.common.ApiResponseDto;
import com.beteam.willu.common.exception.RecruitmentStatusException;
import com.beteam.willu.common.security.UserDetailsImpl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

	private final PostService postService;

	// 게시글 작성
	@PostMapping("/post")
	public ResponseEntity<PostResponseDto> createPost(@RequestBody PostRequestDto postRequestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		PostResponseDto post = postService.createPost(postRequestDto, userDetails.getUser());
		return ResponseEntity.status(201).body(post);
	}

	// 게시글 전체 조회
	@GetMapping("/posts")
	public ResponseEntity<List<PostResponseDto>> getPosts() {
		List<PostResponseDto> result = postService.getPosts();
		return ResponseEntity.ok().body(result);
	}

	// @GetMapping("/posts")
	// public String getPosts(Pageable pageable, Model model) {
	//     Page<PostResponseDto> postPage = postService.getPosts(pageable);
	//     model.addAttribute("postPage", postPage);
	//     return null; // 게시글 목록 view 이름 (html)
	// }

	// 게시글 상세 조회
	@GetMapping("/posts/{id}")
	public PostResponseDto getPost(@PathVariable Long id) {
		return postService.getPost(id);
	}

	//
	//게시글 수정
	@PutMapping("/posts/{id}")
	public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id, @RequestBody PostRequestDto postRequestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		PostResponseDto result = postService.updatePost(id, postRequestDto, userDetails.getUsername());
		return ResponseEntity.status((HttpStatus.OK)).body(result);
	}

	// 게시글 삭제
	@DeleteMapping("/posts/{id}")
	public ResponseEntity<ApiResponseDto> deletePost(@PathVariable Long id,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		try {
			postService.deletePost(id, userDetails.getUser());

			return ResponseEntity.ok().body(new ApiResponseDto("게시글이 삭제되었습니다", 200));
		} catch (EntityNotFoundException e) {
			return ResponseEntity.badRequest().body(new ApiResponseDto("게시글을 찾을 수 없습니다.", 400));
		}
	}

	// 모집완료 -> 모집중
	@PatchMapping("/posts/{id}/activate-recruitment")
	public ResponseEntity<ApiResponseDto> activateRecruitment(@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long id) {
		try {
			postService.activateRecruitment(id, userDetails.getUser());
			return ResponseEntity.ok().body(new ApiResponseDto("모집중으로 변경", 200));
		} catch (EntityNotFoundException e) {
			return ResponseEntity.badRequest().body(new ApiResponseDto("게시글을 찾을 수 없습니다.", 400));
		} catch (RecruitmentStatusException e) {
			return ResponseEntity.badRequest().body(new ApiResponseDto("이미 모집중입니다.", 400));
		}
	}

	// 모집중 -> 모집완료
	@PatchMapping("/posts/{id}/complete-recruitment")
	public ResponseEntity<ApiResponseDto> completeRecruitment(@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long id) {
		try {
			postService.completeRecruitment(id, userDetails.getUser());
			return ResponseEntity.ok().body(new ApiResponseDto("모집완료", 200));
		} catch (EntityNotFoundException e) {
			return ResponseEntity.badRequest().body(new ApiResponseDto("게시글을 찾을 수 없습니다.", 400));
		} catch (RecruitmentStatusException e) {
			return ResponseEntity.badRequest().body(new ApiResponseDto("이미 모집완료 되었습니다.", 400));
		}
	}

	// 게시글 검색
	// TODO 파라매터 받아오는 방법 프론트엔드와 연결 시 확정 가능할 듯
	// size를 선택할 수 있는 기능이 필요한가? ex) 10개, 20개, 30개
	@GetMapping("/search/posts")
	public ResponseEntity<Page<PostResponseDto>> searchPosts(
		@RequestParam("keyword") String keyword, // 검색 키워드 파라미터
		@RequestParam("criteria") String criteria, // 검색 조건 파라미터 (title, username, content 중 하나)
		@RequestParam(value = "page", defaultValue = "0") int page, // 페이지 번호 파라미터 (기본값: 0)
		@RequestParam(value = "size", defaultValue = "10") int size, // 페이지당 항목 수 파라미터 (기본값: 10)
		@RequestParam(value = "recruitment", defaultValue = "false") boolean recruitment // 모집중인 게시글만 검색할 것인가?
	) {
		if (keyword.length() < 2) {
			// 검색어 길이가 2자 미만일 경우 에러 응답을 반환하거나, 다른 처리를 할 수 있습니다.
			return ResponseEntity.badRequest().build();
		}

		Pageable pageable = PageRequest.of(page, size); // 페이지와 항목 수를 기반으로 페이징 정보 생성
		Page<PostResponseDto> searchResultPage = postService.searchPosts(keyword, criteria, recruitment,
			pageable); // 서비스를 통해 검색 실행
		return ResponseEntity.ok().body(searchResultPage); // 검색 결과를 응답에 담아 반환
	}
}