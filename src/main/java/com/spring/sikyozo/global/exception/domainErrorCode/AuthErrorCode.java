package com.spring.sikyozo.global.exception.domainErrorCode;

import com.spring.sikyozo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "인증 토큰이 만료 되었습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    LOGIN_FAILED(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호를 잘못 입력하였습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "DB에 해당 Refresh Token이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
