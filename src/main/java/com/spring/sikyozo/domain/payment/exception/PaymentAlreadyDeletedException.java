package com.spring.sikyozo.domain.payment.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.PaymentErrorCode;

public class PaymentAlreadyDeletedException extends SikyozoException {
    public PaymentAlreadyDeletedException() {
        super(PaymentErrorCode.PAYMENT_ALREADY_DELETED);
    }
}