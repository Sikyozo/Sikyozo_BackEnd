package com.spring.sikyozo.domain.industry.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.IndustryErrorCode;

public class IndustryNotFoundException extends SikyozoException {
    public IndustryNotFoundException() {
        super(IndustryErrorCode.INDUSTRY_NOT_FOUND);
    }
}
