package com.spring.sikyozo.domain.ai.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.AiErrorCode;

public class TextNotFoundException extends SikyozoException {
    public TextNotFoundException() {
        super(AiErrorCode.TEXT_NOT_FOUND);
    }
}
