package com.spring.sikyozo.domain.store.exception;

import com.spring.sikyozo.global.exception.ErrorCode;
import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.StoreErrorCode;

public class StoreNotFoundException extends SikyozoException {
    public StoreNotFoundException() {
        super(StoreErrorCode.STORE_NOT_FOUND);
    }
}
