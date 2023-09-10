package com.beteam.willu.serviceTest;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.beteam.willu.common.jwt.JwtUtil;
import com.beteam.willu.common.redis.RedisUtil;
import com.beteam.willu.user.dto.UserRequestDto;
import com.beteam.willu.user.dto.UserResponseDto;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;
import com.beteam.willu.user.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@DisplayName("USER SERVICE TEST")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@InjectMocks
	private UserService userService;
	@Mock
	private UserRepository userRepository;
	@Spy
	private BCryptPasswordEncoder passwordEncoder;
	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private RedisUtil redisUtil;

	@Mock
	private HttpServletResponse response;

	@DisplayName("signUp")
	@Test
	public void userService_CreateUser_returnUserResponseDto() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String enryptedPw = encoder.encode("password");
		//인코딩

		UserRequestDto requestDto = UserRequestDto.builder()
			.username("testUser44")
			.password(enryptedPw)
			.nickname("testUserNick44")
			.email("testUser44@email.com")
			.build();
		//인코딩
		User user = User.builder()
			.username(requestDto.getUsername())
			.password(enryptedPw)
			.nickname(requestDto.getNickname())
			.email(requestDto.getEmail())
			.build();

		//when((userRepository).save(any(User.class))).thenReturn(user);
		when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
		UserResponseDto savedUser = userService.userSignup(requestDto);

		Assertions.assertThat(savedUser.getNickname()).isEqualTo(requestDto.getNickname());
		Assertions.assertThat(savedUser.getUsername()).isEqualTo(requestDto.getUsername());
		Assertions.assertThat(savedUser.getEmail()).isEqualTo(requestDto.getEmail());

		verify(userRepository, times(1)).save(any(User.class));
		verify(passwordEncoder, times(1)).encode(any(String.class));
	}

	@DisplayName("유저 단일 조회")
	@Test
	public void userService_findUser() {
		//given

		when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
		//when
		final User user = userService.findUser(1L);
		//then
		Assertions.assertThat(user.getNickname()).isEqualTo("testUserNick");
		Assertions.assertThat(user.getUsername()).isEqualTo("testUser");
	}

	@DisplayName("LOGIN")
	@Test
	public void userService_Login() {
		UserRequestDto requestDto = UserRequestDto.builder().username("testUser").password("password").build();
		User user = user();
		String username = user.getUsername();
		when(userRepository.findByUsername(requestDto.getUsername())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).thenReturn(true);

		jwtUtil = Mockito.mock(JwtUtil.class);

		when(jwtUtil.createAccessToken(username)).thenReturn("testAccessToken");
		when(jwtUtil.createRefreshToken(username)).thenReturn("testRefreshToken");

		response = Mockito.mock(HttpServletResponse.class);

		redisUtil = Mockito.mock(RedisUtil.class);

		userService.userLogin(requestDto, response);

		Assertions.assertThat("testAccessToken").isEqualTo(jwtUtil.createAccessToken(username));
		Assertions.assertThat("testRefreshToken").isEqualTo(jwtUtil.createRefreshToken(username));
	}

	private User user() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String enryptedPw = encoder.encode("password");

		return User.builder()
			.username("testUser")
			.password(enryptedPw)
			.nickname("testUserNick")
			.email("testUser@email.com")
			.build();
	}
}
