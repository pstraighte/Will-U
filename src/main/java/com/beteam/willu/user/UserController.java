package com.beteam.willu.user;


import com.beteam.willu.common.ApiResponseDto;
import com.beteam.willu.common.security.UserDetailsImpl;
import com.beteam.willu.user.review.dto.ReviewRequestDto;
import com.beteam.willu.user.review.dto.ReviewResponseDto;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    //관심 유저 추가
    @PostMapping("/interest/{id}")
    public ResponseEntity<ApiResponseDto> addInterest(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            userService.addInterest(id, userDetails.getUser());
        } catch (DuplicateRequestException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("관심 유저를 추가했습니다", HttpStatus.ACCEPTED.value()));
    }

    //관심 유저 삭제
    @DeleteMapping("/interest/{id}")
    public ResponseEntity<ApiResponseDto> removeInterest(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            userService.removeInterest(id, userDetails.getUser());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("관심 유저를 삭제했습니다.", HttpStatus.ACCEPTED.value()));
    }

    //차단 유저 추가
    @PostMapping("/blacklist/{id}")
    public ResponseEntity<ApiResponseDto> addBlacklist(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            userService.addBlacklist(id, userDetails.getUser());
        } catch (DuplicateRequestException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("해당 유저를 차단했습니다", HttpStatus.ACCEPTED.value()));
    }

    //차단 유저 해제
    @DeleteMapping("/blacklist/{id}")
    public ResponseEntity<ApiResponseDto> removeBlacklist(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            userService.removeBlacklist(id, userDetails.getUser());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("차단을 해체했습니다.", HttpStatus.ACCEPTED.value()));
    }

    // 리뷰 작성
    @PostMapping("/review/users/{id}")
    public void createReview(@PathVariable Long id, @RequestBody ReviewRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.createReview(id, requestDto, userDetails);
    }

    // 리뷰 데이터 조회
    @GetMapping("/review/users/{id}")
    public ReviewResponseDto getReviews(@PathVariable Long id) {
        return userService.getReviews(id);
    }
}
