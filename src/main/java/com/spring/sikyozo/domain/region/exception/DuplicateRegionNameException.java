package com.spring.sikyozo.domain.region.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.RegionErrorCode;

public class DuplicateRegionNameException extends SikyozoException {
    public DuplicateRegionNameException() {
        super(RegionErrorCode.DUPLICATE_REGION_NAME);
    }
}
