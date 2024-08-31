package com.spring.sikyozo.domain.store.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.StoreErrorCode;

public class StorePermissionException extends SikyozoException {
    public StorePermissionException() {
        super(StoreErrorCode.STORE_PERMISSION_ERROR);
    }
}
