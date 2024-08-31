package com.spring.sikyozo.domain.payment.dto.request;

import com.spring.sikyozo.domain.payment.entity.PaymentType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProcessPaymentRequestDto {

    private Long userId;
    private UUID paymentId;
    private PaymentType paymentType;
    private Long price;

}
