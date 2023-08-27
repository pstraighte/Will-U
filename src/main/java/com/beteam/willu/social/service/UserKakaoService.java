package com.beteam.willu.social.service;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.beteam.willu.common.jwt.JwtUtil;
import com.beteam.willu.common.redis.RedisUtil;
import com.beteam.willu.social.dto.KakaoUserInfoDto;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "KAKAO Login")
@Service
@RequiredArgsConstructor
public class UserKakaoService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final RestTemplate restTemplate;
	private final RedisUtil redisUtil;
	private final JwtUtil jwtUtil;

	public void kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
		// 1. "인가 코드"로 "액세스 토큰" 요청
		String accessToken = getToken(code);
		// 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
		KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);
		//3, 필요시 회원가입
		User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);
		// 4. JWT 토큰 반환
		String createAccessToken = jwtUtil.createAccessToken(kakaoUser.getUsername());
		String createRefreshToken = jwtUtil.createRefreshToken(kakaoUser.getUsername());
		redisUtil.saveRefreshToken(kakaoUser.getUsername(), createRefreshToken);
		// 쿠키 저장
		jwtUtil.addJwtToCookie(createAccessToken, JwtUtil.AUTHORIZATION_HEADER, response);
		jwtUtil.addJwtToCookie(createRefreshToken, JwtUtil.REFRESH_TOKEN_HEADER, response);
	}

	private String getToken(String code) throws JsonProcessingException {
		log.info("인가코드 : " + code);
		// 요청 URL 만들기
		URI uri = UriComponentsBuilder
			.fromUriString("https://kauth.kakao.com")
			.path("/oauth/token")
			.encode()
			.build()
			.toUri();

		// HTTP Header 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HTTP Body 생성
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", "dbbecd2046b9dd7965998b3f5bfdf389");
		body.add("redirect_uri", "http://localhost:8080/api/users/kakao/callback");
		body.add("code", code);

		RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
			.post(uri)
			.headers(headers)
			.body(body);

		// HTTP 요청 보내기
		ResponseEntity<String> response = restTemplate.exchange(
			requestEntity,
			String.class
		);

		// HTTP 응답 (JSON) -> 액세스 토큰 파싱
		JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
		return jsonNode.get("access_token").asText();
	}

	private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
		log.info("accessToken : " + accessToken);
		// 요청 URL 만들기
		URI uri = UriComponentsBuilder
			.fromUriString("https://kapi.kakao.com")
			.path("/v2/user/me")
			.encode()
			.build()
			.toUri();

		// HTTP Header 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
			.post(uri)
			.headers(headers)
			.body(new LinkedMultiValueMap<>());

		// HTTP 요청 보내기
		ResponseEntity<String> response = restTemplate.exchange(
			requestEntity,
			String.class
		);

		JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

		System.out.println("jsonNode = " + jsonNode);
		Long id = jsonNode.get("id").asLong();
		String nickname = jsonNode.get("properties")
			.get("nickname").asText();
		String email = jsonNode.get("kakao_account")
			.get("email").asText();

		log.info("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);
		return new KakaoUserInfoDto(id, nickname, email);
	}

	private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
		// DB 에 중복된 Kakao Id 가 있는지 확인 (카카오로 이미 로그인을 했었던 상태)
		Long kakaoId = kakaoUserInfo.getId();
		User kakaoUser = userRepository.findByKakaoId(kakaoId).orElse(null);

		if (kakaoUser == null) {
			// 카카오 id가 없는 사용자로 해당 사이트에서 제공하는 방법으로 회원가입한 사용자거나 신규 회원인 경우

			// 카카오 사용자 email 과 동일한 email 가진 회원이 있는지 확인 (해당 사이트에서 제공하느 방법으로 회원 가입한 사용자)
			String kakaoEmail = kakaoUserInfo.getEmail();
			User sameEmailUser = userRepository.findByEmail(kakaoEmail).orElse(null);
			if (sameEmailUser != null) {
				kakaoUser = sameEmailUser;
				// 기존 회원정보에 카카오 Id 추가
				kakaoUser = kakaoUser.kakaoIdUpdate(kakaoId);
			} else {
				// 신규 회원가입
				// password: random UUID
				String password = UUID.randomUUID().toString();
				String encodedPassword = passwordEncoder.encode(password);

				// email: kakao email
				String email = kakaoUserInfo.getEmail();
				String userNickname = kakaoUserInfo.getNickname();
				kakaoUser = User.builder()
					.username(email)
					.nickname(userNickname)
					.password(encodedPassword)
					.email(email)
					.kakaoId(kakaoId)
					.build();
			}

			userRepository.save(kakaoUser);
		}

		// 이미 카카오 id가 있는 유저 - 바로 쿠키를 저장할 데이터를 보내준다.

		return kakaoUser;
	}

}