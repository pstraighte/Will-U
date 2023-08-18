package com.beteam.willu.interest;

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
public class InterestController {

    private final InterestService interestService;

    @PostMapping("/interest/{id}")
    public ResponseEntity<ApiResponseDto> addInterest(@PathVariable Long Id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            interestService.addInterest(Id, userDetails.getUser().getId());
        } catch (DuplicateRequestException e) { //중복실행 방지
            return ResponseEntity.badRequest().body(new ApiResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("관심 유저를 추가했습니다.", HttpStatus.ACCEPTED.value()));
    }

    @DeleteMapping("/interest/{id}")
    public ResponseEntity<ApiResponseDto> removeInterest(@PathVariable Long Id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        try {
            interestService.removeInterest(Id, userDetails.getUser().getId());
        } catch (IllegalArgumentException e) {  //유효값 검사
            return ResponseEntity.badRequest().body(new ApiResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("관심 유저를 삭제했습니다.", HttpStatus.ACCEPTED.value()));
    }



    }
