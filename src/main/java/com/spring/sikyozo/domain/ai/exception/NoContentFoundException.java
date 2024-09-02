package com.spring.sikyozo.domain.ai.exception;

import com.spring.sikyozo.global.exception.ErrorCode;
import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.AiErrorCode;

public class NoContentFoundException extends SikyozoException {
    public NoContentFoundException() {
        super(AiErrorCode.NO_CONTENT_FOUND);
    }
}
