package com.spring.sikyozo.global.exception;

import lombok.Getter;

@Getter
public class SikyozoException extends RuntimeException {
    private final ErrorCode errorCode;

    public SikyozoException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
