package com.beteam.willu.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

// lombok
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor     //테스트에서 사용
public class UserRequestDto {

	private String username;

	private String password;

	private String nickname;

	private String email;

}
