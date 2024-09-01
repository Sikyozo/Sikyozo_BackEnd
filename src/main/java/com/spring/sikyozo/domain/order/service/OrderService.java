package com.spring.sikyozo.domain.order.service;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.sikyozo.domain.address.entity.Address;
import com.spring.sikyozo.domain.address.exception.AddressNotFoundException;
import com.spring.sikyozo.domain.address.repository.AddressRepository;
import com.spring.sikyozo.domain.cart.entity.CartItem;
import com.spring.sikyozo.domain.cart.exception.CartItemNotExistsException;
import com.spring.sikyozo.domain.cart.repository.CartRepository;
import com.spring.sikyozo.domain.menu.entity.Menu;
import com.spring.sikyozo.domain.menu.exception.MenuNotFoundException;
import com.spring.sikyozo.domain.menu.repository.MenuRepositoryImpl;
import com.spring.sikyozo.domain.order.dto.response.*;
import com.spring.sikyozo.domain.order.entity.Order;
import com.spring.sikyozo.domain.order.entity.OrderPaymentStatus;
import com.spring.sikyozo.domain.order.entity.OrderStatus;
import com.spring.sikyozo.domain.order.entity.OrderType;
import com.spring.sikyozo.domain.order.exception.OrderCancelTimeExpiredException;
import com.spring.sikyozo.domain.order.exception.OrderNotFoundException;
import com.spring.sikyozo.domain.order.repository.OrderRepository;
import com.spring.sikyozo.domain.ordermenu.entity.OrderMenu;
import com.spring.sikyozo.domain.store.entity.Store;
import com.spring.sikyozo.domain.store.exception.StoreNotFoundException;
import com.spring.sikyozo.domain.store.repository.StoreRepository;
import com.spring.sikyozo.domain.user.entity.User;
import com.spring.sikyozo.domain.user.entity.UserRole;
import com.spring.sikyozo.domain.user.exception.AccessDeniedException;
import com.spring.sikyozo.domain.user.exception.UserNotFoundException;
import com.spring.sikyozo.domain.user.repository.UserRepository;
import com.spring.sikyozo.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spring.sikyozo.domain.order.entity.QOrder.order;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final JPAQueryFactory jpaQueryFactory;
    private final UserRepository userRepository;
    private final MenuRepositoryImpl menuRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final SecurityUtil securityUtil;


    /*
     * 비대면 주문
     */
    @Transactional
    public CreateOrderResponseDto createOrderByOnline(UUID addressId) {

        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();

        log.info("비대면 주문 요청. userId: {}", loginUserId);

        User user = findUserById(loginUserId);
        Address address = findAddressById(addressId);

        List<CartItem> cartItemList = cartRepository.getCartItemsByUserId(loginUserId);
        verifyCartItemListNotEmpty(loginUserId, cartItemList);
        Set<Store> storeSet = fromCartItemListToStoreSet(cartItemList);

        if (storeSet.size() != 1) {
            log.warn("비대면 주문 시 장바구니 동일 가게 검증 실패. userId: {}", loginUserId);
            throw new AccessDeniedException();
        }

        Store store = storeSet.iterator().next();

        List<OrderMenu> orderMenuList = fromCartItemListToOrderMenuList(cartItemList);

        verifyIsCustomer(user);

        Order order = Order.createOrderByOnline(user, address, store, orderMenuList);
        orderRepository.save(order);

        log.info("비대면 주문 생성 성공. userId: {}", loginUserId);

        return new CreateOrderResponseDto(order.getId(), order.getTotalPrice(), cartItemList);
    }

    private void verifyIsCustomer(User user) {
        if (!isCustomer(user)) {
            log.warn("비대면 주문 시 권한 인증(고객만 가능) 실패. userId: {}", user.getId());
            throw new AccessDeniedException();
        }
    }

    /*
     * 대면 주문
     */
    @Transactional
    public CreateOrderResponseDto createOrderByOffline() {

        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();

        log.info("비대면 주문 요청. userId: {}", loginUserId);

        List<CartItem> cartItemList = cartRepository.getCartItemsByUserId(loginUserId);
        verifyCartItemListNotEmpty(loginUserId, cartItemList);
        Set<Store> storeSet = fromCartItemListToStoreSet(cartItemList);

        if (storeSet.size() != 1) {
            log.warn("대면 주문 시 장바구니 동일 가게 검증 실패. userId: {}", loginUserId);
            throw new AccessDeniedException();
        }

        Store store = storeSet.iterator().next();

        List<OrderMenu> orderMenuList = fromCartItemListToOrderMenuList(cartItemList);

        if (!isOwner(loginUser) || !loginUser.getStores().contains(store)) {
            log.warn("대면 주문 시 권한 인증 실패. userId: {}", loginUserId);
            throw new AccessDeniedException();
        }

        Order order = Order.createOrderByOffline(loginUser, store, orderMenuList);
        orderRepository.save(order);

        log.info("대면 주문 생성 성공. userId: {}", loginUserId);
        return new CreateOrderResponseDto(order.getId(), order.getTotalPrice(), cartItemList);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    private Address findAddressById(UUID addressId) {
        return addressRepository.findById(addressId).orElseThrow(AddressNotFoundException::new);
    }

    private void verifyCartItemListNotEmpty(Long userId, List<CartItem> cartItemList) {
        if (cartItemList.isEmpty()) {
            log.warn("장바구니에 상품이 존재하지 않아 주문 실패. userId: {}", userId);
            throw new CartItemNotExistsException();
        }
    }

    private Set<Store> fromCartItemListToStoreSet(List<CartItem> cartItemList) {
        return cartItemList.stream()
                .map(m -> findMenuByCartItem(m).getStore())
                .collect(Collectors.toSet());
    }

    private Menu findMenuByCartItem(CartItem cartItem) {
        return menuRepository.findById(UUID.fromString(cartItem.getId())).orElseThrow(MenuNotFoundException::new);

    }

    private List<OrderMenu> fromCartItemListToOrderMenuList(List<CartItem> cartItemList) {
        return cartItemList.stream()
                .map(m -> OrderMenu.create(findMenuByCartItem(m), m.getQuantity()))
                .collect(Collectors.toList());
    }

    /*
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(UUID orderId) {

        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();
        Order order = findOrderById(orderId);

        log.info("주문 취소 요청 userId: {}, orderId: {}", loginUserId, orderId);

        if (isCustomer(loginUser)) {
            if (!order.getUser().equals(loginUser)) {
                log.warn("권한 문제로(본인 인증 실패) 인한 주문 취소를 요청 실패 userId: {}, orderId: {}", loginUserId, orderId);
                throw new AccessDeniedException();
            }
            // 5분 제한 검증
            if (order.getStatus().equals(OrderStatus.ACCEPTED) && LocalDateTime.now().isAfter(order.getAcceptedAt().plusMinutes(5))) {
                // 주문 수락 5분 이후면 취소 불가 에러 발생
                log.warn("정책 문제로(5분 지나면 주문 취소 불가) 인한 주문 취소를 요청 실패 userId: {}, orderId: {}", loginUserId, orderId);
                throw new OrderCancelTimeExpiredException();
            }
            order.cancelOrder(loginUser);
            log.info("주문 취소 성공 userId: {}, orderId: {}", loginUserId, orderId);
            return;
        }

        if (isMasterOrManager(loginUser)) {
            order.cancelOrder(loginUser);
            log.info("주문 취소 성공 userId: {}, orderId: {}", loginUserId, orderId);
            return;
        }

        if (isOwner(loginUser) && loginUser.getStores().contains(order.getStore())) {
            order.cancelOrder(loginUser);
            log.info("주문 취소 성공 userId: {}, orderId: {}", loginUserId, orderId);
            return;
        }

        log.warn("주문 실패 userId: {}, orderId: {}", loginUser, orderId);

        throw new AccessDeniedException();
    }

    private boolean isOwner(User loginUser) {
        return loginUser.getRole().equals(UserRole.OWNER);
    }

    private boolean isCustomer(User loginUser) {
        return loginUser.getRole().equals(UserRole.CUSTOMER);
    }

    private boolean isMasterOrManager(User loginUser) {
        return loginUser.getRole().equals(UserRole.MASTER) || loginUser.getRole().equals(UserRole.MANAGER);
    }


    /*
     *  주문 거절
     */
    @Transactional
    public RejectOrderResponseDto rejectOrder(UUID orderId) {

        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();
        Order order = findOrderById(orderId);

        log.info("주문 거절 요청 userId: {}, orderId: {}", loginUserId, orderId);

        if (isOwner(loginUser)) {
            for (Store store : loginUser.getStores()) {
                if (store.getOrders().contains(order)) {
                    order.rejectOrder(loginUser);
                    log.info("주문 거절 성공 userId: {}, orderId: {}", loginUserId, orderId);
                    return new RejectOrderResponseDto(orderId);
                }
            }
        }
        log.warn("주문 실패 userId: {}, orderId: {}", loginUser, orderId);
        throw new AccessDeniedException();
    }

    private Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
    }

    /*
     *  주문 조회
     */
    @Transactional(readOnly = true)
    public Page<GetOrderResponseDto> getOrders(Long userId, UUID storeId, OrderType type, OrderStatus status, OrderPaymentStatus orderPaymentStatus, String show, Pageable pageable) {

        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();

        log.info("주문 조회 요청 loginUserId: {}, userId: {}, storeId", loginUserId, userId, storeId);

        if (isOwner(loginUser) | isCustomer(loginUser)) {
            if (show != null) {
                throw new AccessDeniedException();
            }
        }

        verifyHasPermissionToGetAllOrders(userId, storeId, loginUser);
        verifyHasPermissionToGetStoreOrders(userId, storeId, loginUser);
        verifyHasPermissionToGetUserOrders(userId, storeId, loginUser);

        List<Order> orders = jpaQueryFactory
                .selectFrom(order)
                .where(
                        userEq(userId),
                        storeEq(storeId),
                        typeEq(type),
                        statusEq(status),
                        orderPaymentStatusEq(orderPaymentStatus),
                        showEq(show)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSortOrder(pageable))
                .fetch();


        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(order.count())
                .where(
                        userEq(userId),
                        storeEq(storeId),
                        typeEq(type),
                        statusEq(status),
                        orderPaymentStatusEq(orderPaymentStatus),
                        showEq(show)
                );

        Page<Order> page = PageableExecutionUtils.getPage(orders, pageable, countQuery::fetchOne);
        log.info("주문 정보 조회 성공 loginUserId: {}, userId: {}, storeId: {}", loginUserId, userId, storeId);
        return page.map(GetOrderResponseDto::new);

    }

    /*
     *  단 건 주문 조회
     */
    @Transactional(readOnly = true)
    public GetOrderResponseDto getOrder(UUID orderId) {

        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();
        Order order = findOrderById(orderId);

        log.info("주문 조회 요청 loginUserId: {}, orderId: {}", loginUserId, orderId);

        if (isCustomer(loginUser) && !loginUser.getOrders().contains(order)) {
            throw new AccessDeniedException();
        }

        if (isOwner(loginUser)) {
            for (Store store : loginUser.getStores()) {
                store.getOrders().contains(order);
                log.info("주문 조회 성공 loginUserId: {}, orderId: {}", loginUserId, orderId);
                return new GetOrderResponseDto(order);
            }

        }

        if (isMasterOrManager(loginUser)) {
            log.info("주문 조회 성공 loginUserId: {}, orderId: {}", loginUserId, orderId);
            return new GetOrderResponseDto(order);
        }

        log.warn("주문 조회 실패 loginUserId: {}, orderId: {}", loginUserId, orderId);

        throw new AccessDeniedException();
    }


    private OrderSpecifier<?> getSortOrder(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return null; // 정렬 조건이 없을 때
        }

        Sort.Order sortOrder = pageable.getSort().iterator().next(); // 첫 번째 정렬 기준 사용
        PathBuilder<Order> orderPath = new PathBuilder<>(Order.class, "order");

        return new OrderSpecifier(
                sortOrder.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                orderPath.get(sortOrder.getProperty())
        );
    }

    private BooleanExpression userEq(Long userId) {
        return (userId != null) ? order.user.eq(findUserById(userId)) : null;
    }

    private  BooleanExpression storeEq(UUID storeId) {
        return (storeId != null) ? (order.store.eq(findStoreById(storeId))) : null;
    }

    private  BooleanExpression statusEq(OrderStatus status) {
        return (status != null) ? (order.status.eq(status)) : null;
    }

    private  BooleanExpression typeEq(OrderType type) {
        return (type != null) ? (order.type.eq(type)) : null;
    }

    private  BooleanExpression orderPaymentStatusEq(OrderPaymentStatus orderPaymentStatus) {
        return (orderPaymentStatus != null) ? (order.orderPaymentStatus.eq(orderPaymentStatus)) : null;
    }

    private  BooleanExpression showEq(String hide) {
        if (hide == "all") {
            return null;
        }

        if (hide == "deleted") {
            return order.deletedAt.isNotNull();
        }
        return order.deletedAt.isNull();
    }


    private void verifyHasPermissionToGetUserOrders(Long userId, UUID storeId, User loginUser) {
        if (userId != null && storeId != null) {
            if (isCustomer(loginUser) || isOwner(loginUser)) {
                if (!loginUser.getId().equals(userId)) {
                    log.warn("권한 문제로 인한 유저 주문 조회 실패 loginUserId: {}", loginUser.getId());
                    throw new AccessDeniedException();
                }
            }
        }
    }

    private void verifyHasPermissionToGetStoreOrders(Long userId, UUID storeId, User loginUser) {
        if (userId == null && storeId != null) {
            if (isCustomer(loginUser)) {
                log.warn("권한 문제로 인한 가게 주문 조회 실패 loginUserId: {}", loginUser.getId());
                throw new AccessDeniedException();
            }

            Store store = findStoreById(storeId);

            if (isOwner(loginUser) && !loginUser.getStores().contains(store)) {
                log.warn("권한 문제로 인한 가게 주문 조회 실패 loginUserId: {}", loginUser.getId());
                throw new AccessDeniedException();
            }
        }
    }

    private void verifyHasPermissionToGetAllOrders(Long userId, UUID storeId, User loginUser) {
        if (userId == null && storeId == null) {
            if (!isMasterOrManager(loginUser)) {
                log.warn("권한 문제로 인한 전체 주문 조회 실패 loginUserId: {}", loginUser.getId());
                throw new AccessDeniedException();
            }
        }
    }

    private Store findStoreById(UUID storeId) {
        return storeRepository.findById(storeId).orElseThrow(() -> new StoreNotFoundException());
    }


    /*
     * 주문 수락
     */

    @Transactional
    public AcceptOrderResponseDto acceptOrder(UUID orderId) {

        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();

        log.info("주문 승낙 요청 loginUserId: {}, orderId: {}", loginUserId, orderId);

        if (!isOwner(loginUser)) {
            log.warn("권한 문제로 인한(가게 주인만 가능) 주문 승낙 요청 실패 loginUserId: {}, orderId: {}", loginUserId, orderId);
            throw new AccessDeniedException();
        }

        Order order = findOrderById(orderId);

        if (!isOrderBelongsToUserStores(loginUser, order)) {
            log.warn("권한 문제로 인한(주문 받은 가게 주인만 가능) 주문 승낙 요청 실패 loginUserId: {}, orderId: {}", loginUserId, orderId);
            throw new AccessDeniedException();
        }

        order.acceptOrder();

        log.info("주문 승낙 요청 성공 loginUserId: {}, orderId: {}", loginUserId, orderId);

        return new AcceptOrderResponseDto(orderId);
    }

    private boolean isOrderBelongsToUserStores(User loginUser, Order order) {
        for (Store store : loginUser.getStores()) {
            if (store.getOrders().contains(order)) {
                return true;
            }
        }
        return false;
    }

    /*
     * 주문 처리 완료(배달 완료)
     */

    @Transactional
    public CompleteOrderResponseDto completeOrder(UUID orderId) {

        Order order = findOrderById(orderId);
        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();

        log.info("주문 처리 완료 요청 loginUserId: {}, orderId: {}", loginUserId, orderId);

        if (isOwner(loginUser)) {
            for (Store store : loginUser.getStores()) {
                if (store.getOrders().contains(order)) {
                    order.completeOrder();
                    log.info("주문 처리 완료 요청 성공 loginUserId: {}, orderId: {}", loginUserId, orderId);
                    return new CompleteOrderResponseDto(orderId);
                }
            }
        }

        log.warn("주문 처리 완료 요청 실패 loginUserId: {}, orderId: {}", loginUserId, orderId);

        throw new AccessDeniedException();
    }

    /*
     * 주문 삭제
     */
    @Transactional
    public DeleteOrderResponseDto deleteOrder(UUID orderId) {

        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();
        Order order = findOrderById(orderId);

        log.info("주문 삭제 요청 loginUserId: {}, orderId: {}", loginUserId, orderId);

        if (isCustomer(loginUser)) {
            if (!order.getUser().equals(loginUser)) {
                log.warn("권한 문제로(고객은 본인 주문만 삭제 가능) 주문 삭제 요청 실패 loginUserId: {}, orderId: {}", loginUserId, orderId);
                throw new AccessDeniedException();
            }

            order.delete(loginUser);
            log.info("고객 주문 삭제 요청 성공 loginUserId: {}, orderId: {}", loginUserId, orderId);
            return new DeleteOrderResponseDto(orderId);
        }

        if (isMasterOrManager(loginUser)) {
            order.delete(loginUser);
            log.info("관리자 주문 삭제 요청 성공 loginUserId: {}, orderId: {}", loginUserId, orderId);
            return new DeleteOrderResponseDto(orderId);
        }

        if (isOwner(loginUser) && loginUser.getStores().contains(order.getStore())) {
            order.delete(loginUser);
            log.info("가게 주인 주문 삭제 요청 성공 loginUserId: {}, orderId: {}", loginUserId, orderId);
            return new DeleteOrderResponseDto(orderId);
        }
        log.info("권한 문제로(가게 주인은 본인 가게의 주문만 삭제 가능) 주문 삭제 요청 실패 loginUserId: {}, orderId: {}", loginUserId, orderId);
        throw new AccessDeniedException();
    }


}
