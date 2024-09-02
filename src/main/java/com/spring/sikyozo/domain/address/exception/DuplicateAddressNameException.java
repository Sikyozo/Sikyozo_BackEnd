package com.spring.sikyozo.domain.address.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.AddressErrorCode;

public class DuplicateAddressNameException extends SikyozoException {
    public DuplicateAddressNameException() {
        super(AddressErrorCode.DUPLICATE_ADDRESS_NAME);
    }
}
