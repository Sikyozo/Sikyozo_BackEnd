package com.spring.sikyozo.domain.payment.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.PaymentErrorCode;

public class PaymentAlreadyFailedException extends SikyozoException {
    public PaymentAlreadyFailedException() {
        super(PaymentErrorCode.PAYMENT_ALREADY_FAILED);
    }
}