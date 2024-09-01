package com.spring.sikyozo.domain.order.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.OrderErrorCode;

public class OrderAlreadyCompletedException extends SikyozoException {
    public OrderAlreadyCompletedException() {
        super(OrderErrorCode.ALREADY_COMPLETED);
    }
}
