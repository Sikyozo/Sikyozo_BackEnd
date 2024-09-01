package com.spring.sikyozo.domain.cart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class RemoveFromCartResponseDto {

    private UUID menuId;
}
