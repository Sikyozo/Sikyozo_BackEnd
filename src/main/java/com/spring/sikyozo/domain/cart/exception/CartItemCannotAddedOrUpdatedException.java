package com.spring.sikyozo.domain.cart.exception;

public class CartItemCannotAddedOrUpdatedException extends RuntimeException{
    public CartItemCannotAddedOrUpdatedException(String message) {
        super(message);
    }
}
