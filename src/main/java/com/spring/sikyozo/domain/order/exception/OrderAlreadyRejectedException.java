package com.spring.sikyozo.domain.order.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.OrderErrorCode;

public class OrderAlreadyRejectedException extends SikyozoException {
    public OrderAlreadyRejectedException() {
        super(OrderErrorCode.ALREADY_REJECTED);
    }
}
