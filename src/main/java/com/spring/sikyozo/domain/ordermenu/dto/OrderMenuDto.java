package com.spring.sikyozo.domain.ordermenu.dto;

import com.spring.sikyozo.domain.cart.entity.CartItem;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrderMenuDto {

    private UUID menuId;
    private int quantity;

    public OrderMenuDto(CartItem cartItem) {
        menuId = UUID.fromString(cartItem.getId());
        quantity = cartItem.getQuantity();
    }
}
