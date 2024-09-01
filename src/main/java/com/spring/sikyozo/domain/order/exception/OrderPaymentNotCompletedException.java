package com.spring.sikyozo.domain.order.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.OrderErrorCode;

public class OrderPaymentNotCompletedException extends SikyozoException {
    public OrderPaymentNotCompletedException() {
        super(OrderErrorCode.PAYMENT_NOT_COMPLETED);
    }
}
