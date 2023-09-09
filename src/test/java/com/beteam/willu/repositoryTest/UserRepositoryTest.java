package com.beteam.willu.repositoryTest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.beteam.willu.user.dto.UserUpdateRequestDto;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("UserRepository Test")
public class UserRepositoryTest {
	@Autowired
	private UserRepository userRepository;

	User user;

	@DisplayName("사용자 추가")
	@Test
	void addUser() {
		//given
		user = user();

		//when
		User saveUser = userRepository.save(user);
		//then
		User findUser = userRepository.findById(user.getId()).orElseThrow();

		Assertions.assertThat(user).isSameAs(saveUser);
		Assertions.assertThat(user).isSameAs(findUser);
		Assertions.assertThat(user.getUsername()).isSameAs(saveUser.getUsername());
		Assertions.assertThat(user.getUsername()).isSameAs(findUser.getUsername());
		Assertions.assertThat(saveUser.getId()).isNotNull();
	}

	@DisplayName("사용자 조회 (username,email,nickname")
	@Test
	void getUser() {
		//given
		user = userRepository.save(User.builder()
			.username("testUser123123")
			.password("password")
			.nickname("testUserNick123123")
			.email("testUser123123@email.com")
			.build());

		//when
		//then
		Assertions.assertThat(user.getUsername()).isEqualTo("testUser123123");
		Assertions.assertThat(user.getEmail()).isEqualTo("testUser123123@email.com");
		Assertions.assertThat(user.getNickname()).isEqualTo("testUserNick123123");
	}

	@DisplayName("사용자 수정")
	@Test
	void updateUser() {
		//given
		user = user();
		User saveUser = userRepository.save(user);
		//when
		UserUpdateRequestDto requestDto = new UserUpdateRequestDto("수정 이름1", "수정 닉네임1", "010-4444-4444", "전주", "");
		user.profileUpdate(requestDto);
		//then
		Assertions.assertThat(user).isSameAs(saveUser);
		Assertions.assertThat(saveUser.getPhoneNumber()).isSameAs("010-4444-4444");
		Assertions.assertThat(saveUser.getArea()).isSameAs("전주");
		Assertions.assertThat(saveUser.getUsername()).isNotSameAs("수정 닉네임1");
	}

	@DisplayName("사용자 탈퇴")
	@Test
	void deleteUser() {
		//given
		user = user();
		User saveUser = userRepository.save(user);
		//when
		userRepository.delete(saveUser);
		//then
		Assertions.assertThat(userRepository.findById(saveUser.getId()).isPresent()).isFalse();
	}

	private User user() {
		return User.builder()
			.username("testUser")
			.password("password")
			.nickname("testUserNick")
			.email("testUser@email.com")
			.build();
	}

}
