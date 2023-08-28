package com.beteam.willu.user.service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beteam.willu.common.jwt.JwtUtil;
import com.beteam.willu.common.redis.RedisUtil;
import com.beteam.willu.common.security.UserDetailsImpl;
import com.beteam.willu.user.dto.UserRequestDto;
import com.beteam.willu.user.dto.UserResponseDto;
import com.beteam.willu.user.dto.UserUpdateRequestDto;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "userService")
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final RedisUtil redisUtil;

	public void userSignup(UserRequestDto requestDto) {

		if (userRepository.findByUsername(requestDto.getUsername()).isPresent()) {
			throw new IllegalArgumentException("해당 유저가 이미 있습니다.");
		}

		if (userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
			throw new IllegalArgumentException("중복된 username 입니다");
		}

		// 카카오 계정으로 사용된 email 로 또 회원가입을 진행 할시
		// 또는 중복된 email 이 있는지 확인
		if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
			throw new IllegalArgumentException("중복된 email 입니다");
		}

		String password = passwordEncoder.encode(requestDto.getPassword());

		User user = User.builder()
			.username(requestDto.getUsername())
			.password(password)
			.nickname(requestDto.getNickname())
			.email(requestDto.getEmail())
			.build();

		userRepository.save(user);
	}

	@Transactional
	public void userLogin(UserRequestDto requestDto, HttpServletResponse response) {
		log.info("userService login 진입");
		String username = requestDto.getUsername();
		User user = findUser(username);

		if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("로그인 실패 비밀번호 틀립니다!");
		}

		//토큰 생성
		String accessToken = jwtUtil.createAccessToken(username);
		String refreshToken = jwtUtil.createRefreshToken(username);

		log.info("refresh token: " + refreshToken);
		log.info("access token: " + accessToken);
		// refreshToken redis 저장
		redisUtil.saveRefreshToken(username, refreshToken);

		// accessToken, refreshToken cookie 저장
		jwtUtil.addJwtToCookie(accessToken, JwtUtil.AUTHORIZATION_HEADER, response);
		jwtUtil.addJwtToCookie(refreshToken, JwtUtil.REFRESH_TOKEN_HEADER, response);
	}

	public void logout(String accessToken, HttpServletResponse response, String username) {
		//쿠키에서 가져온 토큰추출
		accessToken = URLDecoder.decode(accessToken, StandardCharsets.UTF_8).substring(7);
		log.info("accessToken 값: " + accessToken);

		if (redisUtil.getRefreshToken(username) != null) {
			log.info("로그아웃 시 리프레시 토큰이 존재하면 지워준다.");
			redisUtil.deleteRefreshToken(username);
		}
		log.info("액세스 토큰 블랙리스트로 저장 : " + accessToken);
		redisUtil.addBlackList(accessToken, jwtUtil.getExpiration(accessToken));
		//쿠키 삭제
		jwtUtil.expireCookie(response, JwtUtil.AUTHORIZATION_HEADER);
		jwtUtil.expireCookie(response, JwtUtil.REFRESH_TOKEN_HEADER);
	}

	// 유저 조회 (프로파일)
	public UserResponseDto getProfile(Long id) {
		User user = findUser(id);
		return new UserResponseDto(user);
	}

	// 유저 업데이트 (프로파일)
	@Transactional
	public UserResponseDto userUpdate(UserUpdateRequestDto updateRequestDto, UserDetailsImpl users) {
		User user = users.getUser();
		user.profileUpdate(updateRequestDto);
		return new UserResponseDto(user);
	}

	@Transactional
	public void deleteUser(Long id, User user) {
		if (!id.equals(user.getId())) {
			throw new IllegalArgumentException("본인이 아닙니다. 탈퇴할 수 없습니다.");
		}
		userRepository.delete(user);
	}

	private User findUser(String username) {
		return userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
	}

	private User findUser(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
	}

}
