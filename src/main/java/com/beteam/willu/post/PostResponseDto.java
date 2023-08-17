package com.beteam.willu.post;

import com.beteam.willu.common.ApiResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponseDto extends ApiResponseDto {

    private String title;
    private String content;
    //TODO
    private String username; // 뭘 넣어야 하나?
    private LocalDateTime promiseTime;
    private String promiseArea;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long maxnum;
    private Long score = 3L;

    public PostResponseDto(Post post) {
        this.title = post.getTitle();
        this.username = post.getUser().getUsername();
        this.content = post.getContent();
        this.promiseTime = post.getPromiseTime();
        this.promiseArea = post.getPromiseArea();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.maxnum = post.getMaxnum();
        this.score = post.getUser().getScore();
    }
}
