package com.beteam.willu.blacklist.dto;

import com.beteam.willu.user.entity.User;

import lombok.Getter;

@Getter
public class BlacklistResponseDto {

	private User receiver;
	private User sender;

	public BlacklistResponseDto(User receiver, User sender) {
		this.receiver = receiver;
		this.sender = sender
		;
	}
}