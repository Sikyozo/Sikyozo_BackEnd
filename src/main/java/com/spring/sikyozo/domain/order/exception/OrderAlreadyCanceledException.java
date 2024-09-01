package com.spring.sikyozo.domain.order.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.OrderErrorCode;

public class OrderAlreadyCanceledException extends SikyozoException {
    public OrderAlreadyCanceledException() {
        super(OrderErrorCode.ALREADY_CANCELED);
    }
}
