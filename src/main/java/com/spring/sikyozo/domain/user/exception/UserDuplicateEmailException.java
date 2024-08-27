package com.spring.sikyozo.domain.user.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.UserErrorCode;

public class UserDuplicateEmailException extends SikyozoException {
    public UserDuplicateEmailException() {
        super(UserErrorCode.DUPLICATE_EMAIL);
    }
}
