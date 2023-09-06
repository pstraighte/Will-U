package com.beteam.willu.user.dto;

import com.beteam.willu.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {
	private String username;
	private String nickname;
	private String phoneNumber;
	private String area;
	private String picture;
	private Double score;

	public UserResponseDto(User user) {
		this.username = user.getUsername();
		this.nickname = user.getNickname();
		this.phoneNumber = user.getPhoneNumber();
		this.area = user.getUsername();
		this.picture = user.getPicture();
		this.score = user.getScore();
	}

}
