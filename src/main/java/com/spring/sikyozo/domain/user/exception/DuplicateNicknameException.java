package com.spring.sikyozo.domain.user.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.UserErrorCode;

public class DuplicateNicknameException extends SikyozoException {
    public DuplicateNicknameException() {
        super(UserErrorCode.DUPLICATE_NICKNAME);
    }
}
