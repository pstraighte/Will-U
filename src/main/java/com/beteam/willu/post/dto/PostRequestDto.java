package com.beteam.willu.post.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    @NotBlank(message = "게시글 제목을 입력해 주세요")
    private String title;

    @NotBlank(message = "게시글 내용을 입력해 주세요")
    private String content;

    private LocalDateTime promiseTime;

    private String promiseArea;

    @Min(2) //최소 2명
    private Long maxnum;

    List<String> tags;
}
