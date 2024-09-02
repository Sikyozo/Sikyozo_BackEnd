package com.spring.sikyozo.domain.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class CompleteOrderResponseDto {
    private UUID orderID;
}
