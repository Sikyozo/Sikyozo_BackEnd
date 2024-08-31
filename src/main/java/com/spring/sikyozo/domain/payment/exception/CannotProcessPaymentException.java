package com.spring.sikyozo.domain.payment.exception;

public class CannotProcessPaymentException extends RuntimeException{
    public CannotProcessPaymentException(String message) {
        super(message);
    }
}
