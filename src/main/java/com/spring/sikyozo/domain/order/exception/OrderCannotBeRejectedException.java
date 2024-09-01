package com.spring.sikyozo.domain.order.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.OrderErrorCode;

public class OrderCannotBeRejectedException extends SikyozoException {
    public OrderCannotBeRejectedException() {
        super(OrderErrorCode.ORDER_CANNOT_BE_REJECTED);
    }
}
