package com.spring.sikyozo.global.security.jwt.login.dto;

import lombok.Getter;

@Getter
public class UserLoginRequestDto {
    private String username;
    private String password;
}
