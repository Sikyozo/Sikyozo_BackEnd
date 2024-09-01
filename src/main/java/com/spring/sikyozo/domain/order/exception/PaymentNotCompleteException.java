package com.spring.sikyozo.domain.order.exception;

public class PaymentNotCompleteException extends RuntimeException{
    public PaymentNotCompleteException(String message) {
        super(message);
    }
}
