package com.spring.sikyozo.domain.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeletePaymentRequestDto {

    @NotNull(message = "paymentId는 필수입니다.")
    private UUID paymentId;
}
