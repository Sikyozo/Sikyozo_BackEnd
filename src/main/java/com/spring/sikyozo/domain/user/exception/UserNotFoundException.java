package com.spring.sikyozo.domain.user.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.UserErrorCode;

public class UserNotFoundException extends SikyozoException {
    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }
}
