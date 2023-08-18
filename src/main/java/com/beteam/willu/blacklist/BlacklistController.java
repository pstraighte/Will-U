package com.beteam.willu.blacklist;

import com.beteam.willu.common.ApiResponseDto;
import com.beteam.willu.security.UserDetailsImpl;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BlacklistController {

    private final BlacklistService blacklistService;

    @PostMapping("/blacklist/{id}") //유저 차단 기능
    public ResponseEntity<ApiResponseDto> addBlacklist(@PathVariable Long Id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            blacklistService.addBlacklist(Id, userDetails.getUser().getId());
        } catch (DuplicateRequestException e) { //중복실행 방지
            return ResponseEntity.badRequest().body(new ApiResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("해당 유저를 차단했습니다.", HttpStatus.ACCEPTED.value()));
    }

    @DeleteMapping("/blacklist/{id}")    //유저 차단 해제 기능
    public ResponseEntity<ApiResponseDto> removeblacklist(@PathVariable Long Id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            blacklistService.removeBlacklist(Id, userDetails.getUser().getId());
        } catch (IllegalArgumentException e) {  //유효값 검사
            return ResponseEntity.badRequest().body(new ApiResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("해당 유저의 차단을 취소했습니다.", HttpStatus.ACCEPTED.value()));
    }
}
