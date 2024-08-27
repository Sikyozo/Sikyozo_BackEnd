package com.spring.sikyozo.domain.user.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.UserErrorCode;

public class UserPasswordMismatchException extends SikyozoException {
    public UserPasswordMismatchException() {
        super(UserErrorCode.PASSWORD_MISMATCH);
    }
}
