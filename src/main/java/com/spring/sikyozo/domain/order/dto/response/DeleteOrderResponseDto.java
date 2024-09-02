package com.spring.sikyozo.domain.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DeleteOrderResponseDto {

    private UUID orderId;
}
