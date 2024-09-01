package com.spring.sikyozo.domain.order.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.OrderErrorCode;

public class OrderCannotBeAcceptedException extends SikyozoException {
    public OrderCannotBeAcceptedException() {
        super(OrderErrorCode.ORDER_CANNOT_BE_ACCEPTED);
    }
}
