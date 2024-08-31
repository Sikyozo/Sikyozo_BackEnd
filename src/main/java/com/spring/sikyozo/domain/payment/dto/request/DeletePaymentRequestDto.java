package com.spring.sikyozo.domain.payment.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeletePaymentRequestDto {

    private Long userId;
    private UUID paymentId;
}
