package com.beteam.willu.interest.dto;

import com.beteam.willu.user.entity.User;

import lombok.Getter;

@Getter
public class InterestResponseDto {

	private User receiver;
	private User sender;

	public InterestResponseDto(User receiver, User sender) {
		this.receiver = receiver;
		this.sender = sender;
	}
}
