package com.spring.sikyozo.domain.order.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateOrderByOnlineRequestDto {

    private Long userId;
    private UUID addressId;
}