package com.spring.sikyozo.domain.user.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.UserErrorCode;

public class UserDuplicateUsernameException extends SikyozoException {
    public UserDuplicateUsernameException() {
        super(UserErrorCode.DUPLICATE_USERNAME);
    }
}
