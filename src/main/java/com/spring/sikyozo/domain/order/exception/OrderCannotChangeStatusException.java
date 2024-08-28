package com.spring.sikyozo.domain.order.exception;

public class OrderCannotChangeStatusException extends RuntimeException{
    public OrderCannotChangeStatusException(String message) {
        super(message);
    }
}
