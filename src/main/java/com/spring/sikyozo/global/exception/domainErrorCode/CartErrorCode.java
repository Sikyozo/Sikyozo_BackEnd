package com.spring.sikyozo.global.exception.domainErrorCode;

import com.spring.sikyozo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CartErrorCode implements ErrorCode {

    CART_ITEM_NOT_EXISTS(HttpStatus.NOT_FOUND, "장바구니에 등록된 상품이 없습니다."),

    ;


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
