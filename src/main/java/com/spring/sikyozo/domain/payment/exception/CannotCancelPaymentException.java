package com.spring.sikyozo.domain.payment.exception;

public class CannotCancelPaymentException extends RuntimeException{
    public CannotCancelPaymentException(String message) {
        super(message);
    }
}
