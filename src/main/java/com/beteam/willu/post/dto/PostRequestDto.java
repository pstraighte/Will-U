package com.beteam.willu.post.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequestDto {

	private String title;
	private String content;
	private LocalDateTime promiseTime;
	private String promiseArea;
	private Long maxnum;
}
