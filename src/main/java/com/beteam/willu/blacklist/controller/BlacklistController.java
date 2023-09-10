package com.beteam.willu.blacklist.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beteam.willu.blacklist.service.BlacklistService;
import com.beteam.willu.common.ApiResponseDto;
import com.beteam.willu.common.security.UserDetailsImpl;
import com.sun.jdi.request.DuplicateRequestException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BlacklistController {
	private final BlacklistService blacklistService;

	//차단 유저 추가
	@PostMapping("/blacklist/{id}")
	public ResponseEntity<ApiResponseDto> addBlacklist(@PathVariable Long id,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		try {
			blacklistService.addBlacklist(id, userDetails.getUser());
		} catch (DuplicateRequestException e) {
			return ResponseEntity.badRequest().body(new ApiResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
		} catch (IllegalArgumentException b) {
			return ResponseEntity.badRequest().body(new ApiResponseDto("자신을 차단 할 수 없습니다.", 400));
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED)
			.body(new ApiResponseDto("해당 유저를 차단했습니다", HttpStatus.ACCEPTED.value()));
	}

	//차단 유저 해제
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
