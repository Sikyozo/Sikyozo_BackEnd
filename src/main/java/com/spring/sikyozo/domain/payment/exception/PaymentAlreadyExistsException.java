package com.spring.sikyozo.domain.payment.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.PaymentErrorCode;

public class PaymentAlreadyExistsException extends SikyozoException {
    public PaymentAlreadyExistsException() {
        super(PaymentErrorCode.PAYMENT_ALREADY_EXISTS);
    }
}
