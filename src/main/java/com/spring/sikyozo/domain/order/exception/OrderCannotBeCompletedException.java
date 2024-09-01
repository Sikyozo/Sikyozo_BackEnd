package com.spring.sikyozo.domain.order.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.OrderErrorCode;

public class OrderCannotBeCompletedException extends SikyozoException {
    public OrderCannotBeCompletedException() {
        super(OrderErrorCode.ORDER_CANNOT_BE_COMPLETED);
    }
}
