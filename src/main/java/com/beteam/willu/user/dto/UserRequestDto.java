package com.beteam.willu.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// lombok
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Builder
@AllArgsConstructor     //테스트에서 사용
public class UserRequestDto {

	private String username;

	private String password;

	private String nickname;

	private String email;

}
