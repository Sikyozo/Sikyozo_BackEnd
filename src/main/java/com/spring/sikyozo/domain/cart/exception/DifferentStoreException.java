package com.spring.sikyozo.domain.cart.exception;

public class DifferentStoreException extends RuntimeException{

    public DifferentStoreException(String message) {
        super(message);
    }
}
