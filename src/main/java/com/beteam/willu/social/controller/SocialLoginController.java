package com.beteam.willu.social.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.beteam.willu.social.service.SocialGoogleService;
import com.beteam.willu.social.service.SocialNaverService;
import com.beteam.willu.social.service.UserKakaoService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class SocialLoginController {

	private final UserKakaoService userKakaoService;
	private final SocialGoogleService socialGoogleService;
	private final SocialNaverService socialNaverService;

	// 카카오 토큰 요청
	// 카카오 로그인이 아닌 페이지 로그인 정보와 비교할 정보 정하기 ex) email
	@GetMapping("/users/kakao/callback")
	public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws
		JsonProcessingException {
		userKakaoService.kakaoLogin(code, response);
		return "redirect:/";
	}

	// 구글 토큰 요청
	@GetMapping("/users/login/oauth2/code/google")
	public String googleLogin(@RequestParam String code, HttpServletResponse response) throws
		JsonProcessingException {
		socialGoogleService.googleLogin(code, response);
		return "redirect:/";
	}

	//네이버 토큰 요청
	@GetMapping("/users/naver/callback")
	public String naverLogin(@RequestParam String code, HttpServletResponse response) throws
		JsonProcessingException {
		socialNaverService.naverLogin(code, response);
		return "redirect:/";
	}

}
