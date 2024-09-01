package com.spring.sikyozo.domain.order.dto.response;

import com.spring.sikyozo.domain.cart.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateOrderResponseDto {

    private UUID orderId;
    private Long totalPrice;
    private List<CartItem> cartItemList;

}
