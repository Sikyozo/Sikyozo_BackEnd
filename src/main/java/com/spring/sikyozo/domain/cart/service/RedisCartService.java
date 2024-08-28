package com.spring.sikyozo.domain.cart.service;

import com.spring.sikyozo.domain.cart.dto.response.GetCartResponseDto;
import com.spring.sikyozo.domain.cart.entity.CartItem;
import com.spring.sikyozo.domain.cart.exception.DifferentStoreException;
import com.spring.sikyozo.domain.cart.exception.MenuDoesNotExistsException;
import com.spring.sikyozo.domain.cart.repository.CartRepository;
import com.spring.sikyozo.domain.menu.entity.Menu;
import com.spring.sikyozo.domain.menu.repository.MenuRepositoryImpl;
import com.spring.sikyozo.global.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCartService implements CartService {

    private final MenuRepositoryImpl menuRepository;
    private final CartRepository cartRepository;

    /**
     * 장바구니에 품목 추가 또는 수량 수정하기
     */
    @Override
    public void addOrUpdateCartItem(Long userId, UUID menuId, Integer quantity) {

        CartItem cartItem = cartRepository.getCartItemByUserIdAndMenuId(userId, menuId);

        // 장바구니에 이미 품목이 있으면 수량만 수정
        if (cartItem != null) {
            log.info("장바구니 수량 변경 userId: {}, menuId: {}, quantity: {}", userId, menuId, quantity);
            cartItem.updateQuantity(quantity);
        } else {
            log.info("장바구니 품목 추가 userId: {}, menuId: {}, quantity: {}", userId, menuId, quantity);

            // 추가하는 품목이 기존 장바구니에 있는 품목과 같은 Store의 품목인지 확인
            Menu menuById = getMenuById(menuId);

            if (cartRepository.isCartNotEmpty(userId)) {
                verifySameStore(userId, menuById);
            }
            cartItem = CartItem.create(menuById, quantity);
        }
        cartRepository.save(userId, menuId, cartItem);
    }

    private Menu getMenuById(UUID menuId) {
        return menuRepository.findById(menuId).orElseThrow(() -> new MenuDoesNotExistsException("메뉴가 존재하지 않습니다."));
    }

    private void verifySameStore(Long userId, Menu menuById) {
        // 첫 번째 품목을 가져와서 비교
        CartItem itemFromRedis = cartRepository.getFirstCartItem(userId).get();

        // 다른 가게의 상품이면 예외 발생
        if (!menuById.getStore().getId().toString().equals(itemFromRedis.getStoreId())) {
            throw new DifferentStoreException("동일한 가게의 상품만 담을 수 있습니다.");
        }
    }

    /**
     * 장바구니 전체 조회하기
     */
    @Override
    public ResponseDto<List<GetCartResponseDto>> getCart(Long userId) {

        log.info("장바구니 조회 userId: {}", userId);

        List<CartItem> cartItemList = cartRepository.getCartItemsByUserId(userId);
        List<GetCartResponseDto> getCartResponseDtoList = cartItemList.stream()
                .map(m -> new GetCartResponseDto(m.getId(), m))
                .collect(Collectors.toList());
        return ResponseDto.success("장바구니 조회에 성공하였습니다", getCartResponseDtoList);
    }


    /**
     * 장바구니에서 품목 제거
     */
    @Override
    public UUID removeItemFromCart(Long userId, UUID menuId) {

        log.info("장바구니에서 품목 제거 userId: {}, menuId: {}", userId, menuId);

        cartRepository.removeItemByUserIdAndMenuId(userId, menuId);

        return menuId;
    }

    /**
     * 장바구니 비우기
     */
    @Override
    public void clearCart(Long userId) {

        log.info("장바구니 비우기 userId: {}", userId);

        cartRepository.deleteCartByUserId(userId);
    }

}
