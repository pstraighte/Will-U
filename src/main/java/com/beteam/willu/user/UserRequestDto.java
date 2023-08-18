package com.beteam.willu.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

// lombok
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequestDto {

    private String username;

    private String password;

    private String nickname;

    private String email;

}
