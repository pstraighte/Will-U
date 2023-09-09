package com.beteam.willu.blacklist.controller;

import com.beteam.willu.blacklist.service.BlacklistService;
import com.beteam.willu.common.ApiResponseDto;
import com.beteam.willu.common.security.UserDetailsImpl;
import com.sun.jdi.request.DuplicateRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Blacklist API", description = "사용자 차단 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BlacklistController {
    private final BlacklistService blacklistService;

    //차단 유저 추가
    @Operation(summary = "사용자 차단", description = "PathVariabl형태의 사용자 id를 이용해 해당 사용자를 차단한다.")
    @PostMapping("/blacklist/{id}")
    public ResponseEntity<ApiResponseDto> addBlacklist(@PathVariable Long id,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            blacklistService.addBlacklist(id, userDetails.getUser());
        } catch (DuplicateRequestException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiResponseDto("해당 유저를 차단했습니다", HttpStatus.ACCEPTED.value()));
    }

    //차단 유저 해제
    @Operation(summary = "사용자 차단 해제", description = "PathVariabl형태의 사용자 id를 이용해 해당 사용자의 차단을 해제한다.")
    @DeleteMapping("/blacklist/{id}")
    public ResponseEntity<ApiResponseDto> removeBlacklist(@PathVariable Long id,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            blacklistService.removeBlacklist(id, userDetails.getUser());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiResponseDto("차단을 해체했습니다.", HttpStatus.ACCEPTED.value()));
    }
}
