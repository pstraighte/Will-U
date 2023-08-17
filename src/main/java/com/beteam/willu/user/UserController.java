package com.beteam.willu.user;


import com.beteam.willu.common.ApiResponseDto;
import com.beteam.willu.security.UserDetailsImpl;
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
    public ResponseEntity<ApiResponseDto> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponseDto("회원 탈퇴 성공", 200));
    }

}
