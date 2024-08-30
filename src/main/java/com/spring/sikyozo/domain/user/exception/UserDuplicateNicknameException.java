package com.spring.sikyozo.domain.user.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.UserErrorCode;

public class UserDuplicateNicknameException extends SikyozoException {
    public UserDuplicateNicknameException() {
        super(UserErrorCode.DUPLICATE_NICKNAME);
    }
}
