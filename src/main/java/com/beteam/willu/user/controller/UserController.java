package com.beteam.willu.user.controller;

import com.beteam.willu.common.ApiResponseDto;
import com.beteam.willu.common.security.UserDetailsImpl;
import com.beteam.willu.user.dto.UserRequestDto;
import com.beteam.willu.user.dto.UserResponseDto;
import com.beteam.willu.user.dto.UserUpdateRequestDto;
import com.beteam.willu.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "User API", description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @Operation(summary = "사용자 회원가입", description = "정해진 파라미터를 받은 후 사용자의 데이터를 저장합니다.")
    @Parameter(name = "username", required = true, schema = @Schema(type = "String"), description = "사용자 아이디")
    @Parameter(name = "nickname", required = true, schema = @Schema(type = "String"), description = "사용자 닉네임")
    @Parameter(name = "password", required = true, schema = @Schema(type = "String"), description = "사용자 비밀번호")
    @Parameter(name = "email", required = true, schema = @Schema(type = "String", format = "email"), description = "사용자 이메일")
    @PostMapping("/users/signup")
    public ResponseEntity<ApiResponseDto> userSignup(@RequestBody UserRequestDto requestDto) {
        userService.userSignup(requestDto);
        return ResponseEntity.status(201).body(new ApiResponseDto("회원가입 성공", 201));
    }

    @Operation(summary = "사용자 로그인", description = "정해진 파라미터를 받은 후 JWT 토큰을 생성합니다.")
    @Parameter(name = "username", required = true, schema = @Schema(type = "String"), description = "사용자 아이디")
    @Parameter(name = "password", required = true, schema = @Schema(type = "String"), description = "사용자 비밀번호")
    @PostMapping("/users/login")
    public ResponseEntity<ApiResponseDto> login(@RequestBody UserRequestDto requestDto, HttpServletResponse response) {
        userService.userLogin(requestDto, response);
        return ResponseEntity.status(200).body(new ApiResponseDto("로그인 성공", 200));
    }

    //로그아웃 확인용 API  redis 에 토큰을 추가하는 행위임으로 POST 사용
    @Operation(summary = "사용자 로그아웃", description = "로그아웃 한 사용자의 리스페시 토큰을 삭제하고 엑세스 토큰을 블랙리스트로 저장한다.")
    @PostMapping("/users/logout")
    public ResponseEntity<ApiResponseDto> logout(@CookieValue(name = "Authorization") String accessToken,
                                                 HttpServletResponse response, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.logout(accessToken, response, userDetails.getUsername());

        return ResponseEntity.ok().body(new ApiResponseDto("로그아웃 성공", 200));
    }

    // 유저 조회 (프로파일)

    @Operation(summary = "사용자의 프로필을 조회합니다.", description = "PathVariable 형태의 id 값을 이용해 id에 해당하는 사용자의 프로필을 조회합니다. ")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfile(id));
    }

    // 유저 업데이트(프로파일)
    @Operation(summary = "사용자 프로필 업데이트", description = "정해진 파라미터를 받은 후 사용자의 프로필 정보를 업데이트합니다.")
    @Parameter(name = "nickname", schema = @Schema(type = "String"), description = "사용자 닉네임")
    @Parameter(name = "picture", schema = @Schema(type = "String"), description = "사용자 프로필 사진 정보")
    @PutMapping("/users")
    public ResponseEntity<UserResponseDto> userUpdate(@RequestBody UserUpdateRequestDto updateRequestDto,
                                                      @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(userService.userUpdate(updateRequestDto, user));
    }

    @Operation(summary = "사용자 탈퇴", description = "PathVariable 형태의 id로 사용자의 id 와 사용자의 비밀번호를 입력받고 사용자의 데이터를 삭제합니다.")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponseDto> deleteUser(@PathVariable Long id,
                                                     @RequestBody Map<String, String> request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String password = request.get("password");

        // 사용자의 비밀번호 확인
        if (!userService.isPasswordCorrect(userDetails.getUser().getId(), password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDto("비밀번호가 일치하지 않습니다.", 401));
        }

        userService.deleteUser(id, userDetails.getUser());
        return ResponseEntity.ok(new ApiResponseDto("회원 탈퇴 성공", 200));
    }

}
