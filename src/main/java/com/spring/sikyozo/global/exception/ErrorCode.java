package com.spring.sikyozo.global.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
