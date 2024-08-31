package com.spring.sikyozo.domain.order.exception;

public class CartItemDoesNotExistsException extends RuntimeException{
    public CartItemDoesNotExistsException(String message) {
        super(message);
    }
}
