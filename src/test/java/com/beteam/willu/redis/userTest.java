package com.beteam.willu.redis;

import com.beteam.willu.user.User;
import com.beteam.willu.user.UserRepository;
import com.beteam.willu.user.UserRequestDto;
import com.beteam.willu.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;

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
        user = User.builder().username("sim34121").password("123").nickname("nick1").build();
        requestDto = new UserRequestDto(user.getUsername(), user.getPassword(), user.getNickname(),user.getEmail());
    }

    @Test
    @DisplayName(value = "회원가입 테스트")
    public void signupTest() {
        userService.userSignup(requestDto);
        assert userRepository.findByUsername(requestDto.getUsername()).orElseThrow().equals(user.getUsername());
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