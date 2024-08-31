package com.spring.sikyozo.domain.industry.exception;

import com.spring.sikyozo.global.exception.ErrorCode;
import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.IndustryErrorCode;

public class EmptyIndustryListException extends SikyozoException {
    public EmptyIndustryListException() {
        super(IndustryErrorCode.EMPTY_INDUSTRY_LIST);
    }
}
