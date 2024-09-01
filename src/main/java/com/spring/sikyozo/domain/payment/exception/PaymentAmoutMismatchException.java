package com.spring.sikyozo.domain.payment.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.PaymentErrorCode;

public class PaymentAmoutMismatchException extends SikyozoException {
    public PaymentAmoutMismatchException() {
        super(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH);
    }
}