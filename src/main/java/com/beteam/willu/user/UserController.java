package com.beteam.willu.user;


import com.beteam.willu.common.ApiResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final UserKakaoService userKakaoService;
    @PostMapping("/users/signup")
    public ResponseEntity<ApiResponseDto> userSignup(@RequestBody UserRequestDto requestDto) {
        userService.userSignup(requestDto);
        return ResponseEntity.status(201).body(new ApiResponseDto("회원가입 성공", 201));
    }

    @PostMapping("/users/login")
    public ResponseEntity<ApiResponseDto> login(@RequestBody UserRequestDto requestDto, HttpServletResponse response) {
        userService.userLogin(requestDto, response);
        return ResponseEntity.ok().body(new ApiResponseDto("로그인 성공", 200));
    }

    //로그아웃 확인용 API  redis 에 토큰을 추가하는 행위임으로 POST 사용
    @PostMapping("/users/logout")
    public ResponseEntity<ApiResponseDto> logout(@CookieValue(name = "Authorization") String accessToken, HttpServletResponse response, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.logout(accessToken, response, userDetails.getUsername());

        return ResponseEntity.ok().body(new ApiResponseDto("로그아웃 성공", 200));
    }

    // 유저 조회 (프로파일)
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfile(id));
    }

    // 유저 업데이트(프로파일)
    @PutMapping("/users")
    public ResponseEntity<UserResponseDto> userUpdate(@RequestBody UserUpdateRequestDto updateRequestDto, @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(userService.userUpdate(updateRequestDto, user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponseDto> deleteUser(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.deleteUser(id, userDetails.getUser());
        return ResponseEntity.ok(new ApiResponseDto("회원 탈퇴 성공", 200));
    }

    // 카카오 토큰 요청
    // 카카오 로그인이 아닌 페이지 로그인 정보와 비교할 정보 정하기 ex) email
    @GetMapping("/users/kakao/callback")
    public ResponseEntity<ApiResponseDto> kakaoiLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
       userKakaoService.kakaoLogin(code, response);
       return ResponseEntity.ok().body(new ApiResponseDto("로그인 성공", 200));
    }

    // 로그인 페이지
    @GetMapping("/users/user-login")
    public String getLoginPage() {
        return "login";
    }


}
