package com.spring.sikyozo.domain.cart.repository;

import com.spring.sikyozo.domain.cart.entity.CartItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository {

    void save(Long userId, UUID menuId, CartItem cartItem);

    CartItem getCartItemByUserIdAndMenuId(Long userId, UUID menuId);
    boolean isCartNotEmpty(Long userId);

    Optional<CartItem> getFirstCartItem(Long userId);


    void deleteCartByUserId(Long userId);


    void removeItemByUserIdAndMenuId(Long userId, UUID menuId);

    List<CartItem> getCartItemsByUserId(Long userId);
}
