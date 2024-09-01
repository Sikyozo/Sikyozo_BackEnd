package com.spring.sikyozo.domain.order.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.OrderErrorCode;

public class OrderAlreadyDeletedException extends SikyozoException {
    public OrderAlreadyDeletedException() {
        super(OrderErrorCode.ORDER_ALREADY_DELETED);
    }
}
