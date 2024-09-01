package com.spring.sikyozo.domain.order.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.OrderErrorCode;

public class OrderNotPendingException extends SikyozoException {
    public OrderNotPendingException() {
        super(OrderErrorCode.ORDER_NOT_PENDING);
    }
}

