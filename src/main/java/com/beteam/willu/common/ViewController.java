package com.beteam.willu.common;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.beteam.willu.blacklist.dto.BlacklistResponseDto;
import com.beteam.willu.blacklist.entity.Blacklist;
import com.beteam.willu.blacklist.repository.BlacklistRepository;
import com.beteam.willu.common.security.UserDetailsImpl;
import com.beteam.willu.interest.dto.InterestResponseDto;
import com.beteam.willu.interest.entity.Interest;
import com.beteam.willu.interest.repository.InterestRepository;
import com.beteam.willu.post.dto.PostResponseDto;
import com.beteam.willu.post.repository.PostRepository;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ViewController {

	private final InterestRepository interestRepository;
	private final BlacklistRepository blacklistRepository;
	private final PostRepository postRepository;
	private final UserService userService;

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

	@GetMapping("/users/profile/{id}")
	public String Profile(Model model, @PathVariable Long id) {
		User user = userService.findUser(id);
		model.addAttribute("user", user);

		return "profile";
	}
}