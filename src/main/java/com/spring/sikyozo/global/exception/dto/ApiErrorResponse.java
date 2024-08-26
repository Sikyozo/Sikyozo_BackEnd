package com.spring.sikyozo.global.exception.dto;

import com.spring.sikyozo.global.exception.ErrorCode;
import com.spring.sikyozo.global.exception.ErrorCodeDetail;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiErrorResponse extends ApiResponse {
    private final ErrorCodeDetail error;

    private ApiErrorResponse(HttpStatus status, String path, String exception, String code, ErrorCode errorCode){
        super(false, status, path);
        this.error = ErrorCodeDetail.of(exception, code, errorCode);
    }

    // ApiErrorResponse 객체를 생성하고 반환
    public static ApiErrorResponse of(HttpStatus status, String path, String exception, String code, ErrorCode errorCode){
        return new ApiErrorResponse(status, path, exception, code, errorCode);
    }
}
