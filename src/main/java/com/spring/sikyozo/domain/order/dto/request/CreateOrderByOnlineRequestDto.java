package com.spring.sikyozo.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateOrderByOnlineRequestDto {

    @NotNull(message = "addressId는 필수 입니다.")
    private UUID addressId;
}