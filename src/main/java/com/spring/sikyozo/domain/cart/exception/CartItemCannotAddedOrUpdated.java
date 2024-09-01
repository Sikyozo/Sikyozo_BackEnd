package com.spring.sikyozo.domain.cart.exception;

public class CartItemCannotAddedOrUpdated extends RuntimeException{
    public CartItemCannotAddedOrUpdated(String message) {
        super(message);
    }
}
