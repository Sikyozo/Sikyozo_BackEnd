package com.spring.sikyozo.global.exception.domainErrorCode;

import com.spring.sikyozo.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum StoreErrorCode implements ErrorCode {
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_NOT_FOUND","해당 가게를 찾을 수 없습니다."),
    STORE_PERMISSION_ERROR(HttpStatus.BAD_REQUEST, "STORE_PERMISSION_ERROR", "해당 기능을 사용할 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    StoreErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
