package com.spring.sikyozo.domain.ai.exception;

import com.spring.sikyozo.global.exception.ErrorCode;
import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.AiErrorCode;

public class TextExtractionFailedException extends SikyozoException {
    public TextExtractionFailedException() {
        super(AiErrorCode.TEXT_EXTRACTION_FAILED);
    }
}
