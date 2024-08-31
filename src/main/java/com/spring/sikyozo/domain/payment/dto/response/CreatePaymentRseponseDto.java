package com.spring.sikyozo.domain.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreatePaymentRseponseDto {
    private UUID paymentId;
    private UUID orderId;
    private Long price;
}
