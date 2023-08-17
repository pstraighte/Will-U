package com.beteam.willu.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostRequestDto {

    private String title;
    private String content;
    private LocalDateTime promiseTime;
    private String promiseArea;
}
