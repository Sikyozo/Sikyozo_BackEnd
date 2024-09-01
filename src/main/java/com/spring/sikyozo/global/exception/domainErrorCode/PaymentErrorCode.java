package com.spring.sikyozo.global.exception.domainErrorCode;

import com.spring.sikyozo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
        PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 결제 정보를 찾을 수 없습니다."),
        PAYMENT_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "이미 처리된 결제입니다."),
        PAYMENT_ALREADY_FAILED(HttpStatus.BAD_REQUEST, "이미 실패한 결제입니다."),
        PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "결제 금액이 일치하지 않습니다."),
        PAYMENT_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "이미 취소된 결제입니다."),
        PAYMENT_ALREADY_DELETED(HttpStatus.FORBIDDEN, "이미 삭제된 결제 정보입니다."),

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
