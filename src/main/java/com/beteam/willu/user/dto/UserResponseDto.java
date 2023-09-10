package com.beteam.willu.user.dto;

import com.beteam.willu.user.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {
	private String username;
	private String nickname;
	private String phoneNumber;
	private String picture;
	private String email;
	@Builder.Default
	private Double score = 0d;

	public UserResponseDto(User user) {
		this.username = user.getUsername();
		this.nickname = user.getNickname();
		this.phoneNumber = user.getPhoneNumber();
		this.picture = user.getPicture();
		this.email = user.getEmail();
		this.score = user.getScore();
	}

}
