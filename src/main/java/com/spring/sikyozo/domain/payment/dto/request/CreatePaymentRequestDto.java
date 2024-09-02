package com.spring.sikyozo.domain.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentRequestDto {

    @NotNull(message = "orderId는 필수입니다.")
    private UUID orderId;

    @NotNull(message = "price는 필수입니다.")
    private Long price;
}
