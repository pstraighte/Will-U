package com.beteam.willu.post.dto;

import com.beteam.willu.common.ApiResponseDto;
import com.beteam.willu.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MinimalPostResponseDto extends ApiResponseDto {
	private final Long id;
	private final String title;
	private final String nickname;
	private final Long maxnum;
	private Boolean recruitment;

	public MinimalPostResponseDto(Post post) {
		this.id = post.getId();
		this.title = post.getTitle();
		this.nickname = post.getUser().getNickname();
		this.maxnum = post.getMaxnum();
		this.recruitment = post.getRecruitment();
	}
}
