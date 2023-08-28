package com.beteam.willu.redis;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;

import com.beteam.willu.user.dto.UserRequestDto;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;
import com.beteam.willu.user.service.UserService;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@DisplayName(value = "userTest")
public class userTest {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;

	User user;
	UserRequestDto requestDto;

	@BeforeAll
	public void init() {
		user = User.builder()
			.username("sim34121")
			.password("123")
			.nickname("nick1")
			.email("sim3412@sparta.com")
			.build();
		requestDto = new UserRequestDto(user.getUsername(), user.getPassword(), user.getNickname(), user.getEmail());
	}

	@Test
	@DisplayName(value = "회원가입 테스트")
	public void signupTest() {
		userService.userSignup(requestDto);
		assert userRepository.findByUsername(requestDto.getUsername())
			.orElseThrow()
			.getUsername()
			.equals(user.getUsername());
	}

	@Test
	@Disabled
	@DisplayName(value = "redis 입력 테스트")
	public void testStrings() {

		//given
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		String key = "accessToken";

		//when
		valueOperations.set(key, "test");

		//then
		String value = valueOperations.get(key);
		Assertions.assertThat(value).isEqualTo("test");
	}

}