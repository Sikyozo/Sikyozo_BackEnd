package com.spring.sikyozo.domain.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ProcessPaymentResponseDto {
    private UUID paymentId;
    private Long totalPrice;
}
