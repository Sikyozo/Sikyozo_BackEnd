package com.spring.sikyozo.domain.cart.repository;

import com.spring.sikyozo.domain.cart.entity.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RdisCartRepository implements CartRepository{

    private static final String CART_KEY_PREFIX = "cart:";
    private final RedisTemplate<String, Object> redisTemplate;
    @Override
    public void save(Long userId, UUID menuId, CartItem cartItem) {
        String cartKey = createCartKey(userId);
        redisTemplate.opsForHash().put(cartKey, menuId.toString(), cartItem);
    }

    @Override
    public CartItem getCartItemByUserIdAndMenuId(Long userId, UUID menuId) {
        return (CartItem) redisTemplate.opsForHash().get(createCartKey(userId), menuId.toString());
    }

    @Override
    public boolean isCartNotEmpty(Long userId) {
        return redisTemplate.opsForHash().size(createCartKey(userId)) > 0;
    }

    @Override
    public Optional<CartItem> getFirstCartItem(Long userId) {
        String cartKey = createCartKey(userId);
        return redisTemplate.opsForHash().values(cartKey).stream()
                .map(item -> (CartItem) item)
                .findFirst();
    }

    @Override
    public void deleteCartByUserId(Long userId) {
        redisTemplate.delete(createCartKey(userId));
    }

    @Override
    public void removeItemByUserIdAndMenuId(Long userId, UUID menuId) {
        redisTemplate.opsForHash().delete(createCartKey(userId), menuId.toString());
    }

    @Override
    public List<CartItem> getCartItemsByUserId(Long userId) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(createCartKey(userId));

        return entries.values().stream()
                .map(value -> (CartItem) value)
                .collect(Collectors.toList());
    }


    private String createCartKey(Long userId) {
        return CART_KEY_PREFIX + userId;
    }
}
