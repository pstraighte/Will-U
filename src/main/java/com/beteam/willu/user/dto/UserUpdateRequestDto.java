package com.beteam.willu.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateRequestDto {
	private String username;
	private String nickname;
	private String phoneNumber;
	private String area;
	private String picture;
}
