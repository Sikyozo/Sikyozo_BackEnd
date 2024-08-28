package com.spring.sikyozo.global.security.jwt.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.AuthErrorCode;

public class RefreshTokenNotFoundException extends SikyozoException {
    public RefreshTokenNotFoundException() {
        super(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }
}
