package com.spring.sikyozo.domain.order.exception;

public class CannotCreateOrderException extends RuntimeException{
    public CannotCreateOrderException(String message) {
        super(message);
    }
}
