package com.beteam.willu.post.dto;

import java.time.LocalDateTime;

import com.beteam.willu.common.ApiResponseDto;
import com.beteam.willu.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponseDto extends ApiResponseDto {
	private final Long id;
	private final String title;
	private final String content;
	private final String username; // 뭘 넣어야 하나?
	private final LocalDateTime promiseTime;
	private final String promiseArea;
	private final LocalDateTime createdAt;
	private final LocalDateTime modifiedAt;
	private final Long maxnum;
	private final String category;
	private final boolean recruitment;
	private final Double score;
	private Boolean recruitment = true;

	public PostResponseDto(Post post) {
		this.id = post.getId();
		this.title = post.getTitle();
		this.username = post.getUser().getUsername();
		this.content = post.getContent();
		this.promiseTime = post.getPromiseTime();
		this.promiseArea = post.getPromiseArea();
		this.createdAt = post.getCreatedAt();
		this.modifiedAt = post.getModifiedAt();
		this.maxnum = post.getMaxnum();
		this.recruitment = post.getRecruitment();
		this.score = post.getUser().getScore();
		this.category = post.getCategory();
		this.recruitment = post.getRecruitment();
	}
}
