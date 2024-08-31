package com.spring.sikyozo.domain.payment.exception;

public class CannotCreatePaymentException extends RuntimeException{
    public CannotCreatePaymentException(String message) {
        super(message);
    }
}
