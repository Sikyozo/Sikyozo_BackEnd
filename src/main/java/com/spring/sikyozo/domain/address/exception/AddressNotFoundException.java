package com.spring.sikyozo.domain.address.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.AddressErrorCode;

public class AddressNotFoundException extends SikyozoException {
    public AddressNotFoundException() {
        super(AddressErrorCode.ADDRESS_NOT_FOUND);
    }
}
