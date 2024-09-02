package com.spring.sikyozo.domain.order.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.OrderErrorCode;

public class OrderCancelTimeExpiredException extends SikyozoException {
    public OrderCancelTimeExpiredException() {
        super(OrderErrorCode.ORDER_CANCEL_TIME_EXPIRED);
    }
}
