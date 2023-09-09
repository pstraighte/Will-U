package com.beteam.willu.user.dto;

import lombok.Getter;

@Getter
public class UserUpdateRequestDto {
    private String username;
    private String nickname;
    private String phoneNumber;
    private String area;
    private String picture;
}
