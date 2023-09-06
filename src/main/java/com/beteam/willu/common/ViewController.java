package com.beteam.willu.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.beteam.willu.blacklist.dto.BlacklistResponseDto;
import com.beteam.willu.blacklist.entity.Blacklist;
import com.beteam.willu.blacklist.repository.BlacklistRepository;
import com.beteam.willu.common.jwt.JwtUtil;
import com.beteam.willu.common.security.UserDetailsImpl;
import com.beteam.willu.interest.dto.InterestResponseDto;
import com.beteam.willu.interest.entity.Interest;
import com.beteam.willu.interest.repository.InterestRepository;
import com.beteam.willu.post.dto.PostResponseDto;
import com.beteam.willu.post.repository.PostRepository;
import com.beteam.willu.post.service.PostService;
import com.beteam.willu.review.dto.ReviewSetResponseDto;
import com.beteam.willu.review.repository.ReviewRepository;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ViewController {

	private final InterestRepository interestRepository;
	private final BlacklistRepository blacklistRepository;
	private final PostRepository postRepository;
	private final UserService userService;
	private final PostService postService;
	private final JwtUtil jwtUtil;
	private final ReviewRepository reviewRepository;

	//인덱스 페이지 게시글 목록
	@GetMapping("/")    //주소 입력값
	public String postsView(Model model,
		@RequestParam(value = "page", defaultValue = "0") int page, // 페이지 번호 파라미터 (기본값: 0)
		@RequestParam(value = "size", defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size); // 페이지와 항목 수를 기반으로 페이징 정보 생성
		Page<PostResponseDto> posts = postService.getPosts(pageable);

		model.addAttribute("posts", posts);
		return "index"; //출력 html
	}

	//게시글 작성
	@GetMapping("/post/create")
	public String createPost() {
		return "createPost";
	}

	//게시글 단건 조회
	@GetMapping("/posts/{postId}")
	public String detailPost(Model model, @PathVariable Long postId) {
		PostResponseDto post = postService.getPost(postId);
		model.addAttribute("post", post);
		return "detailPost";
	}

	// 보드 수정 페이지
	@GetMapping("/posts/update/{postId}")
	public String updatePost(Model model, @PathVariable Long postId) {
		model.addAttribute("postId", postId);//2 값을 1에 담음 타임리프 가져올꺼면 이름 "postId" 로 가져오기
		return "updatePost";
	}

	// 마이 페이지
	@GetMapping("/users/mypage")
	public String myPage(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		// userDetails 객체에서 현재 사용자의 정보를 가져와서 모델에 추가
		model.addAttribute("user", userDetails.getUser());

		List<Interest> interests = interestRepository.findBySenderId(userDetails.getUser().getId());

		List<InterestResponseDto> interestResponseDtos = new ArrayList<>();

		for (Interest interest : interests) {
			InterestResponseDto dto = new InterestResponseDto(interest.getReceiver(), interest.getSender());
			interestResponseDtos.add(dto);
		}
		model.addAttribute("interests", interestResponseDtos);

		List<Blacklist> Blacklists = blacklistRepository.findBySenderId(userDetails.getUser().getId());

		List<BlacklistResponseDto> blacklistResponseDtos = new ArrayList<>();

		for (Blacklist blacklist : Blacklists) {
			BlacklistResponseDto dto = new BlacklistResponseDto(blacklist.getReceiver(), blacklist.getSender());
			blacklistResponseDtos.add(dto);
		}
		model.addAttribute("blacklists", blacklistResponseDtos);

		List<PostResponseDto> postResponseDtos = postRepository.findByUser(userDetails.getUser())
			.stream()
			.map(PostResponseDto::new)
			.toList();
		model.addAttribute("posts", postResponseDtos);

		return "mypage";
	}

	@GetMapping("/profile/{id}")
	public String Profile(Model model, @PathVariable Long id) {

		User user = userService.findUser(id);
		model.addAttribute("user", user);

		Double score = user.getScore();

		BigDecimal bd = new BigDecimal(score);

		bd = bd.setScale(2, RoundingMode.HALF_UP);

		if (bd.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
			// 소수 부분이 0일 경우 정수 부분만 사용
			Integer userScore = bd.intValue();
			model.addAttribute("userScore", userScore);
		} else {
			// 소수 부분이 0이 아닌 경우 BigDecimal 값을 그대로 사용
			Double userScore = bd.doubleValue();
			model.addAttribute("userScore", userScore);
		}

		List<ReviewSetResponseDto> receiveReviewResponseDtos = reviewRepository.findByReceiverId(id)
			.stream()
			.map(ReviewSetResponseDto::new)
			.toList();
		model.addAttribute("receiveReviews", receiveReviewResponseDtos);

		List<ReviewSetResponseDto> sendReviewResponseDtos = reviewRepository.findBySenderId(id)
			.stream()
			.map(ReviewSetResponseDto::new)
			.toList();
		model.addAttribute("sendReviews", sendReviewResponseDtos);

		return "profile";
	}

	// 로그인 페이지
	@GetMapping("/login")
	public String getLoginPage(HttpServletResponse response) {
		jwtUtil.expireCookie(response, "Authorization");
		jwtUtil.expireCookie(response, "RT");
		return "loginSignUp";
	}

	// 채팅 페이지
	@GetMapping("/users/chat")
	public String getChatPage(@RequestParam(value = "number", required = false) int number) {
		return "chatting";
	}
}
