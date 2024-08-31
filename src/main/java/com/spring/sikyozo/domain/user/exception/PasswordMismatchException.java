package com.spring.sikyozo.domain.user.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.UserErrorCode;

public class PasswordMismatchException extends SikyozoException {
    public PasswordMismatchException() {
        super(UserErrorCode.PASSWORD_MISMATCH);
    }
}
