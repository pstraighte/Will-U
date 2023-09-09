package com.beteam.willu.post.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {

	private String title;
	private String content;
	private LocalDateTime promiseTime;
	private String promiseArea;
	private Long maxnum;
	private String category;
}
