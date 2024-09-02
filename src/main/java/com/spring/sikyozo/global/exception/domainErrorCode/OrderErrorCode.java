package com.spring.sikyozo.global.exception.domainErrorCode;

import com.spring.sikyozo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문 정보를 찾을 수 없습니다."),
    ORDER_CANCEL_TIME_EXPIRED(HttpStatus.UNAUTHORIZED, "주문 승낙 후 5분이 지나면 주문을 취소할 수 없습니다."),
    ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "이미 완료된 주문은 취소할 수 없습니다."),
    ALREADY_REJECTED(HttpStatus.BAD_REQUEST, "이미 거절된 주문입니다."),
    ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "이미 취소된 주문입니다."),
    ORDER_NOT_PENDING(HttpStatus.BAD_REQUEST, "대기 중인 주문만 처리할 수 있습니다."),
    PAYMENT_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "결제가 완료된 주문만 처리할 수 있습니다."),
    ACCEPTED_ORDER_CANNOT_BE_DELETED(HttpStatus.FORBIDDEN, "수락된 주문은 삭제할 수 없습니다."),
    ORDER_CANNOT_BE_ACCEPTED(HttpStatus.BAD_REQUEST, "주문을 수락할 수 없습니다."),
    ORDER_CANNOT_BE_REJECTED(HttpStatus.BAD_REQUEST, "대기 중이거나 완료된 주문은 거절할 수 없습니다."),
    ORDER_CANNOT_BE_COMPLETED(HttpStatus.BAD_REQUEST, "주문 수락 상태에서만 완료가 가능합니다."),
    ORDER_ALREADY_DELETED(HttpStatus.FORBIDDEN, "이미 삭제된 주문입니다.");

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
