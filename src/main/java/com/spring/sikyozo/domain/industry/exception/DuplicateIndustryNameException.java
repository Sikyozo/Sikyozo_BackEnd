package com.spring.sikyozo.domain.industry.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.IndustryErrorCode;

public class DuplicateIndustryNameException extends SikyozoException {
    public DuplicateIndustryNameException() {
        super(IndustryErrorCode.DUPLICATE_INDUSTRY_NAME);
    }
}
