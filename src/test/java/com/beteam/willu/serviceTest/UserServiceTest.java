package com.beteam.willu.serviceTest;

import static org.mockito.Mockito.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.beteam.willu.user.dto.UserRequestDto;
import com.beteam.willu.user.dto.UserResponseDto;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;
import com.beteam.willu.user.service.UserService;

@DisplayName("USER SERVICE TEST")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@InjectMocks
	private UserService userService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEncoder passwordEncoder;

	@DisplayName("test1")
	@Test
	public void userService_CreateUser_returnUserResponseDto() {
		//인코딩
		User user = User.builder()
			.username("testUser44")
			.password("password")
			.nickname("testUserNick44")
			.email("testUser44@email.com")
			.build();
		UserRequestDto requestDto = UserRequestDto.builder().username("testUser44")
			.password("encrypted")
			.nickname("testUserNick44")
			.email("testUser44@email.com")
			.build();
		when(passwordEncoder.encode(Mockito.any())).thenReturn("encrypted");
		when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
		UserResponseDto responseDto = userService.userSignup(requestDto);

		Assertions.assertThat(responseDto).isNotNull();

	}
}
