package com.beteam.willu.user;

import com.beteam.willu.security.JwtUtil;
import com.beteam.willu.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "userService")
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    public void userSignup(UserRequestDto requestDto) {

        if (userRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("해당 유저가 이미 있습니다.");
        }

        if (userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
            throw new IllegalArgumentException("중복된 username 입니다");
        }

        String password = passwordEncoder.encode(requestDto.getPassword());

        User user = User.builder().username(requestDto.getUsername()).password(password).nickname(requestDto.getNickname()).build();

        userRepository.save(user);
    }

    public void userLogin(UserRequestDto requestDto, HttpServletResponse response) {
        String username = requestDto.getUsername();
        User user = findUser(username);
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("로그인 실패 비밀번호 틀립니다!");
        }
        jwtUtil.addJwtToCookie(jwtUtil.createAccessToken(username), response);

    }

    public void logout(String accessToken) {
        accessToken = URLDecoder.decode(accessToken).substring(7);
        log.info(accessToken);
        //엑세스 토큰 남은 유효시간
        Long expiration = jwtUtil.getExpiration(accessToken);
        log.info("logout 진행 중. 토큰 남은 유효시간: " + expiration);

        //Redis Cache 에 저장
        redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

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
    public void deleteUser(Long id) {
        User user = findUser(id);
        userRepository.delete(user);
    }


    private User findUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    }

}
