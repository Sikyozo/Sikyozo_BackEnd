package com.spring.sikyozo.global.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorCodeDetail {
    private final String exception;
    private final String code;
    private final String message;

    public static ErrorCodeDetail of(String exception, String code, ErrorCode errorCode){
        return new ErrorCodeDetail(exception, code, errorCode.getMessage());
    }

    public static ErrorCodeDetail from(ErrorCode errorCode) {
        return new ErrorCodeDetail(
                "UnknownException",
                errorCode.getCode(),
                errorCode.getMessage()
        );
    }
}
