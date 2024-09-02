package com.spring.sikyozo.domain.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddOrUpdateCartItemRequestDto {

    @NotNull(message = "menuId는 필수입니다.")
    private UUID menuId;
    @NotNull
    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    private int quantity;
}
