package com.spring.sikyozo.global.exception.domainErrorCode;

import com.spring.sikyozo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AiErrorCode implements ErrorCode {
    TEXT_NOT_FOUND(HttpStatus.NOT_FOUND, "텍스트를 찾을 수 없습니다."),
    NO_CONTENT_FOUND(HttpStatus.NOT_FOUND, "응답에서 콘텐츠를 찾을 수 없습니다"),
    TEXT_EXTRACTION_FAILED(HttpStatus.CONFLICT, "응답에서 텍스트를 추출하는 데 실패했습니다");

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
