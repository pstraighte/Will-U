package com.beteam.willu.social.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.beteam.willu.common.ApiResponseDto;
import com.beteam.willu.common.jwt.JwtUtil;
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
	private final JwtUtil jwtUtil;

	// 카카오 토큰 요청
	// 카카오 로그인이 아닌 페이지 로그인 정보와 비교할 정보 정하기 ex) email
	@GetMapping("/users/kakao/callback")
	public ResponseEntity<ApiResponseDto> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws
		JsonProcessingException {
		userKakaoService.kakaoLogin(code, response);
		return ResponseEntity.ok().body(new ApiResponseDto("로그인 성공", 200));
	}

	// 구글 토큰 요청
	@GetMapping("/users/login/oauth2/code/google")
	public ResponseEntity<ApiResponseDto> googleLogin(@RequestParam String code, HttpServletResponse response) throws
		JsonProcessingException {
		socialGoogleService.googleLogin(code, response);
		return ResponseEntity.ok().body(new ApiResponseDto("구글 토큰 요청 성공", 200));
	}

	//네이버 토큰 요청
	@GetMapping("/users/naver/callback")
	public ResponseEntity<ApiResponseDto> naverLogin(@RequestParam String code, HttpServletResponse response) throws
		JsonProcessingException {
		socialNaverService.naverLogin(code, response);
		return ResponseEntity.ok().body(new ApiResponseDto("네이버 토큰 요청 성공", 200));
	}

	// 로그인 페이지
	@GetMapping("/users/user-login")
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

	// 채팅 목록 페이지
	@GetMapping("/users/chatList")
	public String chatList() {
		return "chatTest";
	}

}
