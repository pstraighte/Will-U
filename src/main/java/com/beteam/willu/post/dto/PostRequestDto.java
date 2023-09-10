package com.beteam.willu.post.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class PostRequestDto {

	private String title;
	private String content;
	private LocalDateTime promiseTime;
	private String promiseArea;
	private Long maxnum;
	private String category;
}
