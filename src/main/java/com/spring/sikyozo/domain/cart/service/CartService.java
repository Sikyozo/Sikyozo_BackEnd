package com.spring.sikyozo.domain.cart.service;

import com.spring.sikyozo.domain.cart.dto.response.GetCartResponseDto;
import com.spring.sikyozo.domain.cart.dto.response.RemoveFromCartResponseDto;

import java.util.List;
import java.util.UUID;

public interface CartService {
    void addOrUpdateCartItem(UUID menuId, Integer quantity);

    RemoveFromCartResponseDto removeItemFromCart(UUID menuId);

    List<GetCartResponseDto> getCart();

    void clearCart();
}
