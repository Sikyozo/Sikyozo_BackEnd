package com.spring.sikyozo.domain.cart.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.CartErrorCode;

public class CartItemNotExistsException extends SikyozoException {
    public CartItemNotExistsException() {
        super(CartErrorCode.CART_ITEM_NOT_EXISTS);
    }
}
