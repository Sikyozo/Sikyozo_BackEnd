package com.spring.sikyozo.domain.user.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.UserErrorCode;

public class AccessDeniedException extends SikyozoException {
    public AccessDeniedException() {
        super(UserErrorCode.ACCESS_DENIED);
    }
}
