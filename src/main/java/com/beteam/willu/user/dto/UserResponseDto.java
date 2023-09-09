package com.beteam.willu.user.dto;

import com.beteam.willu.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@Builder
@ToString
@AllArgsConstructor
public class UserResponseDto {
	private String username;
	private String nickname;
	private String phoneNumber;
	private String area;
	private String picture;
	@Builder.Default
	private Double score = 0d;

	public UserResponseDto(User user) {
		this.username = user.getUsername();
		this.nickname = user.getNickname();
		this.phoneNumber = user.getPhoneNumber();
		this.area = user.getUsername();
		this.picture = user.getPicture();
		this.score = user.getScore();
	}

}
