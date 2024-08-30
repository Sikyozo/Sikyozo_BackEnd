package com.spring.sikyozo.global.security.jwt.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.AuthErrorCode;

public class JwtExpiredException extends SikyozoException {
    public JwtExpiredException() {
        super(AuthErrorCode.JWT_EXPIRED);
    }
}
