package com.spring.sikyozo.domain.user.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.UserErrorCode;

public class InvalidRoleException extends SikyozoException {
    public InvalidRoleException() {
        super(UserErrorCode.INVALID_ROLE);
    }
}
