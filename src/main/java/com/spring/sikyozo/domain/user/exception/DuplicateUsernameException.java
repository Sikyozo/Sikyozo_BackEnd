package com.spring.sikyozo.domain.user.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.UserErrorCode;

public class DuplicateUsernameException extends SikyozoException {
    public DuplicateUsernameException() {
        super(UserErrorCode.DUPLICATE_USERNAME);
    }
}
