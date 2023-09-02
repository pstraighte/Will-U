package com.beteam.willu.social.service;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
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
import com.beteam.willu.social.dto.GoogleUserInfoDto;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "GOOGLE Login")
@Service
@RequiredArgsConstructor
public class SocialGoogleService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final RestTemplate restTemplate;
	private final RedisUtil redisUtil;
	private final JwtUtil jwtUtil;

	public void googleLogin(String code, HttpServletResponse response) throws JsonProcessingException {
		// 1. "인가 코드"로 "액세스 토큰" 요청
		String accessToken = getGoogleToken(code);

		// 2. 토큰으로 구글 API 호출 : "액세스 토큰"으로 "구글 사용자 정보" 가져오기
		GoogleUserInfoDto googleUserInfoDto = getgoogleUserInfo(accessToken);

		//3, 필요시 회원가입
		User googleUser = registerGoogleIfNeeded(googleUserInfoDto);

		// 4. JWT 토큰 반환
		String createAccessToken = jwtUtil.createAccessToken(googleUser.getUsername());
		String createRefreshToken = jwtUtil.createRefreshToken(googleUser.getUsername());
		redisUtil.saveRefreshToken(googleUser.getUsername(), createRefreshToken);
		// 쿠키 저장
		jwtUtil.addJwtToCookie(createAccessToken, JwtUtil.AUTHORIZATION_HEADER, response);
		jwtUtil.addJwtToCookie(createRefreshToken, JwtUtil.REFRESH_TOKEN_HEADER, response);

	}

	@Value("${goole.client.id}")
	private String googleClientid;

	@Value("${goole.client.secret}")
	private String googleClientSecret;

	@Value("${aws.ec2.url}")
	private String ec2Url;

	private String getGoogleToken(String code) throws JsonProcessingException {
		log.info("구글인가코드 : " + code);
		// 요청 URL 만들기
		URI uri = UriComponentsBuilder
			.fromUriString("https://oauth2.googleapis.com")
			.path("/token")
			.encode()
			.build()
			.toUri();

		// HTTP Header 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HTTP Body 생성
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", googleClientid);
		body.add("client_secret", googleClientSecret);
		body.add("redirect_uri", ec2Url + "/api/users/login/oauth2/code/google");
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

		System.out.println("GooglejsonNode = " + jsonNode);
		return jsonNode.get("access_token").asText();
	}

	private GoogleUserInfoDto getgoogleUserInfo(String accessToken) throws JsonProcessingException {
		log.info("accessToken : " + accessToken);
		// 요청 URL 만들기
		URI uri = UriComponentsBuilder
			.fromUriString("https://www.googleapis.com")
			.path("/oauth2/v2/userinfo")
			.encode()
			.build()
			.toUri();

		// HTTP Header 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);

		RequestEntity<Void> requestEntity = RequestEntity
			.get(uri)
			.headers(headers)
			.build();

		// HTTP 요청 보내기
		ResponseEntity<String> response = restTemplate.exchange(
			requestEntity,
			String.class
		);

		JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

		System.out.println("jsonNode = " + jsonNode);
		String id = jsonNode.get("id").asText();
		String nickname = jsonNode.get("name")
			.asText();
		String email = jsonNode.get("email")
			.asText();

		log.info("구글 사용자 정보: " + id + ", " + nickname + ", " + email);

		return new GoogleUserInfoDto(id, nickname, email);
	}

	private User registerGoogleIfNeeded(GoogleUserInfoDto googleUserInfoDto) {
		// DB 에 중복된 구글 Id 가 있는지 확인 (구글로 이미 로그인을 했었던 상태)
		String googleId = googleUserInfoDto.getId();
		User googleUser = userRepository.findByGoogleId(googleId).orElse(null);

		if (googleUser == null) {
			// 구글 id가 없는 사용자로 해당 사이트에서 제공하는 방법으로 회원가입한 사용자거나 신규 회원인 경우

			// 구글 사용자 email 과 동일한 email 가진 회원이 있는지 확인 (해당 사이트에서 제공하느 방법으로 회원 가입한 사용자)
			String googleEmail = googleUserInfoDto.getEmail();
			User sameEmailUser = userRepository.findByEmail(googleEmail).orElse(null);
			if (sameEmailUser != null) {
				googleUser = sameEmailUser;
				// 기존 회원정보에 구글 Id 추가
				googleUser = googleUser.googleIdUpdate(googleId);
			} else {
				// 신규 회원가입
				// password: random UUID
				String password = UUID.randomUUID().toString();
				String encodedPassword = passwordEncoder.encode(password);

				// email: 구글 email
				String email = googleUserInfoDto.getEmail();
				String nickName = googleUserInfoDto.getNickname();
				googleUser = User.builder()
					.username(email)
					.nickname(nickName)
					.password(encodedPassword)
					.email(email)
					.googleId(googleId)
					.build();
			}

			userRepository.save(googleUser);
		}

		// 이미 구글 id가 있는 유저 - 바로 쿠키를 저장할 데이터를 보내준다.

		return googleUser;
	}
}

