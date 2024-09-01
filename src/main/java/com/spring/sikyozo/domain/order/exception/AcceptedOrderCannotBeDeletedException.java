package com.spring.sikyozo.domain.order.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.OrderErrorCode;

public class AcceptedOrderCannotBeDeletedException extends SikyozoException {
    public AcceptedOrderCannotBeDeletedException() {
        super(OrderErrorCode.ACCEPTED_ORDER_CANNOT_BE_DELETED);
    }
}
