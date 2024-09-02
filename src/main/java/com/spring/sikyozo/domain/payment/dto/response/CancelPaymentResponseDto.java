package com.spring.sikyozo.domain.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CancelPaymentResponseDto {

    private Long UserId;
    private UUID paymentId;
    private UUID orderId;
    private Long price;
}
