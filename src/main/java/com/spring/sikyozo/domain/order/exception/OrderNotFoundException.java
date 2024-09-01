package com.spring.sikyozo.domain.order.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.OrderErrorCode;
import com.spring.sikyozo.global.exception.domainErrorCode.UserErrorCode;

public class OrderNotFoundException extends SikyozoException {
    public OrderNotFoundException() {
        super(OrderErrorCode.ORDER_NOT_FOUND);
    }
}
