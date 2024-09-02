package com.spring.sikyozo.domain.payment.dto.request;

import com.spring.sikyozo.domain.payment.entity.PaymentType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProcessPaymentRequestDto {

    @NotNull(message = "paymentId는 필수입니다.")
    private UUID paymentId;

    @NotNull(message = "paymentType은 필수입니다.")
    private PaymentType paymentType;

    @NotNull(message = "price는 필수입니다.")
    private Long price;

}
