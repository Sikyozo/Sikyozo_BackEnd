package com.spring.sikyozo.domain.review.exception;

import com.spring.sikyozo.global.exception.ErrorCode;
import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.ReviewErrorCode;

public class InvalidRatingException extends SikyozoException {
    public InvalidRatingException() {
        super(ReviewErrorCode.INVALID_RATING);
    }
}
