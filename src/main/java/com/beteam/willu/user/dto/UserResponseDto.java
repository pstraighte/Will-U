package com.beteam.willu.user.dto;

import com.beteam.willu.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {
	private Long id;
	private String username;
	private String nickname;
	private String picture;
	private Double score;

	public UserResponseDto(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.nickname = user.getNickname();
		this.picture = user.getPicture();
		this.score = user.getScore();
	}

}
