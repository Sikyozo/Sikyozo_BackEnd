package com.spring.sikyozo.domain.cart.service;

import com.spring.sikyozo.domain.cart.dto.response.GetCartResponseDto;
import com.spring.sikyozo.domain.cart.dto.response.RemoveFromCartResponseDto;
import com.spring.sikyozo.domain.cart.entity.CartItem;
import com.spring.sikyozo.domain.cart.repository.CartRepository;
import com.spring.sikyozo.domain.menu.entity.Menu;
import com.spring.sikyozo.domain.menu.exception.MenuNotFoundException;
import com.spring.sikyozo.domain.menu.repository.MenuRepositoryImpl;
import com.spring.sikyozo.domain.store.entity.Store;
import com.spring.sikyozo.domain.user.entity.User;
import com.spring.sikyozo.domain.user.entity.UserRole;
import com.spring.sikyozo.domain.user.exception.AccessDeniedException;
import com.spring.sikyozo.domain.user.exception.UserNotFoundException;
import com.spring.sikyozo.domain.user.repository.UserRepository;
import com.spring.sikyozo.global.security.SecurityUtil;
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
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    /**
     * 장바구니에 품목 추가 또는 수량 수정하기
     */
    @Override
    public void addOrUpdateCartItem(UUID menuId, Integer quantity) {

        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();

        log.info("장바구니 상품 추가, 수정 요청 loginUserId: {}, menuId: {}, quantity: {}", loginUserId, menuId, quantity);

        CartItem cartItem = cartRepository.getCartItemByUserIdAndMenuId(loginUserId, menuId);

        checkUserRoleForCart(loginUser);

        // 장바구니에 이미 품목이 있으면 수량만 수정
        if (cartItem != null) {
            log.info("장바구니 수량 변경 loginUser: {}, menuId: {}, quantity: {}", loginUserId, menuId, quantity);
            cartItem.updateQuantity(quantity);
        } else {
            log.info("장바구니 품목 추가 loginUser: {}, menuId: {}, quantity: {}", loginUserId, menuId, quantity);

            // 추가하는 품목이 기존 장바구니에 있는 품목과 같은 Store의 품목인지 확인
            Menu menuById = getMenuById(menuId);

            // 가게 주인의 경우 본인 가게 메뉴인지 확인
            Store store = menuById.getStore();
            checkOwnerStore(loginUser, store);

            if (cartRepository.isCartNotEmpty(loginUserId)) {
                verifySameStore(loginUserId, menuById);
            }
            cartItem = CartItem.create(menuById, quantity);
        }

        log.info("장바구니 상품 추가, 수정 요청 loginUserId: {}, menuId: {}, quantity: {}", loginUserId, menuId, quantity);

        cartRepository.save(loginUserId, menuId, cartItem);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    private void checkUserRoleForCart(User user) {
        if (user.getRole().equals(UserRole.MASTER) || user.getRole().equals(UserRole.MANAGER)) {
            throw new AccessDeniedException();
        }
    }
    private void checkOwnerStore(User user, Store store) {
        if (user.getRole().equals(UserRole.OWNER) && !user.getStores().contains(store)) {
            throw new AccessDeniedException();
        }
    }

    private Menu getMenuById(UUID menuId) {
        return menuRepository.findById(menuId).orElseThrow(MenuNotFoundException::new);
    }

    private void verifySameStore(Long userId, Menu menuById) {
        // 첫 번째 품목을 가져와서 비교
        CartItem itemFromRedis = cartRepository.getFirstCartItem(userId).get();

        // 다른 가게의 상품이면 예외 발생
        if (!menuById.getStore().getId().toString().equals(itemFromRedis.getStoreId())) {
            log.warn("정책 문제로(동일 가게 상품만 가능) 장바구니 물건 추가, 수정 요청 실패 loginUserId: {}, menuId: {}", userId, menuById);
            throw new AccessDeniedException();
        }
    }

    /**
     * 장바구니 전체 조회하기
     */
    @Override
    public List<GetCartResponseDto> getCart() {

        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();

        log.info("장바구니 조회 요청 loginUserId: {}", loginUserId);

        checkUserRoleForCart(loginUser);

        log.info("장바구니 조회 loginUserId: {}", loginUserId);

        List<CartItem> cartItemList = cartRepository.getCartItemsByUserId(loginUserId);
        List<GetCartResponseDto> getCartResponseDtoList = cartItemList.stream()
                .map(m -> new GetCartResponseDto(m.getId(), m))
                .collect(Collectors.toList());

        log.info("장바구니 조회 요청 성공 loginUserId: {}", loginUserId);

        return getCartResponseDtoList;
    }


    /**
     * 장바구니에서 품목 제거
     */
    @Override
    public RemoveFromCartResponseDto removeItemFromCart(UUID menuId) {

        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();

        log.info("장바구니에서 품목 제거 요청 userId: {}, menuId: {}", loginUserId, menuId);

        checkUserRoleForCart(loginUser);

        cartRepository.removeItemByUserIdAndMenuId(loginUserId, menuId);

        log.info("장바구니 조회 요청 성공 loginUserId: {}, menuId: {}", loginUserId, menuId);

        return new RemoveFromCartResponseDto(menuId);
    }

    /**
     * 장바구니 비우기
     */
    @Override
    public void clearCart() {

        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();

        log.info("장바구니 비우기 요청 userId: {}", loginUserId);

        User user = findUserById(loginUserId);
        checkUserRoleForCart(loginUser);

        log.info("장바구니 비우기 성공 userId: {}", loginUserId);

        cartRepository.deleteCartByUserId(loginUserId);
    }

}
