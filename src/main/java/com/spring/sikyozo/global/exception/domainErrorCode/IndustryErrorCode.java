package com.spring.sikyozo.global.exception.domainErrorCode;

import com.spring.sikyozo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum IndustryErrorCode implements ErrorCode {
    INDUSTRY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 업종을 찾을 수 없습니다."),
    DUPLICATE_INDUSTRY_NAME(HttpStatus.CONFLICT, "중복된 업종이 존재합니다."),
    EMPTY_INDUSTRY_LIST(HttpStatus.BAD_REQUEST, "업종 목록이 비어있습니다.");

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
