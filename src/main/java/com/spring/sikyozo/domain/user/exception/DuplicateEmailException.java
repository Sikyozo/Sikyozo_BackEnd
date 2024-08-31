package com.spring.sikyozo.domain.user.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.UserErrorCode;

public class DuplicateEmailException extends SikyozoException {
    public DuplicateEmailException() {
        super(UserErrorCode.DUPLICATE_EMAIL);
    }
}
