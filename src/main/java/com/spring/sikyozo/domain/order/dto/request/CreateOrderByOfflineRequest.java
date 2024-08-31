package com.spring.sikyozo.domain.order.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateOrderByOfflineRequest {
    private Long userId;
}
