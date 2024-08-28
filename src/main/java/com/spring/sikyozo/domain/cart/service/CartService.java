package com.spring.sikyozo.domain.cart.service;

import com.spring.sikyozo.domain.cart.dto.response.GetCartResponseDto;
import com.spring.sikyozo.global.dto.ResponseDto;

import java.util.List;
import java.util.UUID;

public interface CartService {
    void addOrUpdateCartItem(Long userId, UUID menuId, Integer quantity);

    UUID removeItemFromCart(Long userId, UUID menuId);

    ResponseDto<List<GetCartResponseDto>> getCart(Long userId);

    void clearCart(Long userId);
}
