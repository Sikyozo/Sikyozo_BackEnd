package com.spring.sikyozo.domain.payment.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.PaymentErrorCode;

public class PaymentAlreadyCanceledException extends SikyozoException {
    public PaymentAlreadyCanceledException() {
        super(PaymentErrorCode.PAYMENT_ALREADY_CANCELED);
    }
}
