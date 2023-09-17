package com.beteam.willu.interest.controller;

import com.beteam.willu.common.ApiResponseDto;
import com.beteam.willu.common.security.UserDetailsImpl;
import com.beteam.willu.interest.service.InterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class InterestController {
    private final InterestService interestService;

    //관심 유저 추가
    @PostMapping("/interest/{id}")
    public ResponseEntity<ApiResponseDto> addInterest(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        interestService.addInterest(id, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiResponseDto("관심 유저를 추가했습니다", HttpStatus.ACCEPTED.value()));
    }

    //관심 유저 삭제
    @DeleteMapping("/interest/{id}")
    public ResponseEntity<ApiResponseDto> removeInterest(@PathVariable Long id,
                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        interestService.removeInterest(id, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiResponseDto("관심 유저를 삭제했습니다.", HttpStatus.ACCEPTED.value()));
    }
}
