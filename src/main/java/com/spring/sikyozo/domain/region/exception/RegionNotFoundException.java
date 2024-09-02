package com.spring.sikyozo.domain.region.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.RegionErrorCode;

public class RegionNotFoundException extends SikyozoException {
    public RegionNotFoundException() {
        super(RegionErrorCode.REGION_NOT_FOUND);
    }
}
