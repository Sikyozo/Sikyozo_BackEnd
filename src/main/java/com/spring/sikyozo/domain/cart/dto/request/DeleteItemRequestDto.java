package com.spring.sikyozo.domain.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteItemRequestDto {

    @NotNull(message = "menuId는 필수입니다.")
    private UUID menuId;
}
