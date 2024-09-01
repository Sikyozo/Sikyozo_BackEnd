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
import com.spring.sikyozo.domain.cart.exception.DifferentStoreException;
import com.spring.sikyozo.domain.cart.repository.CartRepository;
import com.spring.sikyozo.domain.menu.entity.Menu;
import com.spring.sikyozo.domain.menu.exception.MenuNotFoundException;
import com.spring.sikyozo.domain.menu.repository.MenuRepositoryImpl;
import com.spring.sikyozo.domain.order.dto.response.*;
import com.spring.sikyozo.domain.order.entity.Order;
import com.spring.sikyozo.domain.order.entity.OrderPaymentStatus;
import com.spring.sikyozo.domain.order.entity.OrderStatus;
import com.spring.sikyozo.domain.order.entity.OrderType;
import com.spring.sikyozo.domain.order.exception.CannotCreateOrderException;
import com.spring.sikyozo.domain.order.exception.CartItemDoesNotExistsException;
import com.spring.sikyozo.domain.order.exception.OrderCannotChangeStatusException;
import com.spring.sikyozo.domain.order.exception.OrderNotFoundException;
import com.spring.sikyozo.domain.order.repository.OrderRepository;
import com.spring.sikyozo.domain.ordermenu.entity.OrderMenu;
import com.spring.sikyozo.domain.store.entity.Store;
import com.spring.sikyozo.domain.store.exception.StoreNotFoundException;
import com.spring.sikyozo.domain.store.repository.StoreRepository;
import com.spring.sikyozo.domain.user.entity.User;
import com.spring.sikyozo.domain.user.entity.UserRole;
import com.spring.sikyozo.domain.user.exception.UserNotFoundException;
import com.spring.sikyozo.domain.user.exception.UserNotHasPermissionException;
import com.spring.sikyozo.domain.user.repository.UserRepository;
import com.spring.sikyozo.global.dto.ResponseDto;
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


    /*
     * 비대면 주문
     */
    @Transactional
    public ResponseDto<CreateOrderResponseDto> createOrderByOnline(Long loginUserId, UUID addressId) {

        log.info("비대면 주문 요청. userId: {}", loginUserId);

        User user = findUserById(loginUserId);
        Address address = findAddressById(addressId);

        List<CartItem> cartItemList = cartRepository.getCartItemsByUserId(loginUserId);
        verifyCartItemListNotEmpty(loginUserId, cartItemList);
        Set<Store> storeSet = fromCartItemListToStoreSet(cartItemList);

        if (storeSet.size() != 1) {
            log.warn("비대면 주문 시 장바구니 동일 가게 검증 실패. userId: {}", loginUserId);
            throw new DifferentStoreException("다른 가게의 상품을 주문할 수 없습니다.");
        }

        Store store = storeSet.iterator().next();

        List<OrderMenu> orderMenuList = fromCartItemListToOrderMenuList(cartItemList);

        verifyIsCustomer(user);

        Order order = Order.createOrderByOnline(user, address, store, orderMenuList);
        orderRepository.save(order);

        log.info("비대면 주문 생성 성공. userId: {}", loginUserId);

        return ResponseDto.success("주문을 완료했습니다.", new CreateOrderResponseDto(order.getId(), order.getTotalPrice(), cartItemList));
    }

    private void verifyIsCustomer(User user) {
        if (!isCustomer(user)) {
            log.warn("비대면 주문 시 권한 인증(고객만 가능) 실패. userId: {}", user.getId());
            throw new CannotCreateOrderException("CUSTOMER 만 주문이 가능합니다.");
        }
    }

    /*
     * 대면 주문
     */
    @Transactional
    public ResponseDto<CreateOrderResponseDto> createOrderByOffline(Long loginUserId) {

        log.info("비대면 주문 요청. userId: {}", loginUserId);

        User loginUser = findUserById(loginUserId);

        List<CartItem> cartItemList = cartRepository.getCartItemsByUserId(loginUserId);
        verifyCartItemListNotEmpty(loginUserId, cartItemList);
        Set<Store> storeSet = fromCartItemListToStoreSet(cartItemList);

        if (storeSet.size() != 1) {
            log.warn("대면 주문 시 장바구니 동일 가게 검증 실패. userId: {}", loginUserId);
            throw new DifferentStoreException("다른 가게의 상품을 주문할 수 없습니다.");
        }

        Store store = storeSet.iterator().next();

        List<OrderMenu> orderMenuList = fromCartItemListToOrderMenuList(cartItemList);

        if (!isOwner(loginUser) || !loginUser.getStores().contains(store)) {
            log.warn("대면 주문 시 권한 인증 실패. userId: {}", loginUserId);
            throw new CannotCreateOrderException("주문 권한이 없습니다.");
        }

        Order order = Order.createOrderByOffline(loginUser, store, orderMenuList);
        orderRepository.save(order);

        log.info("대면 주문 생성 성공. userId: {}", loginUserId);
        return ResponseDto.success("주문을 완료햇습니다.", new CreateOrderResponseDto(order.getId(), order.getTotalPrice(), cartItemList));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
    }

    private Address findAddressById(UUID addressId) {
        return addressRepository.findById(addressId).orElseThrow(() -> new AddressNotFoundException("존재하지 않는 배달지입니다."));
    }

    private void verifyCartItemListNotEmpty(Long userId, List<CartItem> cartItemList) {
        if (cartItemList.isEmpty()) {
            log.warn("장바구니에 상품이 존재하지 않아 주문 실패. userId: {}", userId);
            throw new CartItemDoesNotExistsException("장바구니에 상품이 존재하지 않습니다.");
        }
    }

    private Set<Store> fromCartItemListToStoreSet(List<CartItem> cartItemList) {
        return cartItemList.stream()
                .map(m -> findMenuByCartItem(m).getStore())
                .collect(Collectors.toSet());
    }

    private Menu findMenuByCartItem(CartItem cartItem) {
        return menuRepository.findById(UUID.fromString(cartItem.getId())).orElseThrow(() -> new MenuNotFoundException());

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
    public ResponseDto<String> cancelOrder(UUID orderId, Long loginUserId) {

        log.info("주문 취소 요청 userId: {}, orderId: {}", loginUserId, orderId);

        User loginUser = findUserById(loginUserId);
        Order order = findOrderById(orderId);

        if (isCustomer(loginUser)) {
            if (!order.getUser().equals(loginUser)) {
                log.warn("권한 문제로(본인 인증 실패) 인한 주문 취소를 요청 실패 userId: {}, orderId: {}", loginUserId, orderId);
                throw new OrderCannotChangeStatusException("본인의 주문만 취소할 수 있습니다.");
            }
            // 5분 제한 검증
            if (order.getStatus().equals(OrderStatus.ACCEPTED) && LocalDateTime.now().isAfter(order.getAcceptedAt().plusMinutes(5))) {
                // 주문 수락 5분 이후면 취소 불가 에러 발생
                log.warn("정책 문제로(5분 지나면 주문 취소 불가) 인한 주문 취소를 요청 실패 userId: {}, orderId: {}", loginUserId, orderId);
                throw new OrderCannotChangeStatusException("주문 승낙 후 5분이 지나면 주문을 취소할 수 없습니다.");
            }
            order.cancelOrder(loginUser);
            log.info("주문 취소 성공 userId: {}, orderId: {}", loginUserId, orderId);
            return ResponseDto.success("주문을 취소했습니다.");
        }

        if (isMasterOrManager(loginUser)) {
            order.cancelOrder(loginUser);
            log.info("주문 취소 성공 userId: {}, orderId: {}", loginUserId, orderId);
            return ResponseDto.success("주문을 취소했습니다.");
        }

        if (isOwner(loginUser) && loginUser.getStores().contains(order.getStore())) {
            order.cancelOrder(loginUser);
            log.info("주문 취소 성공 userId: {}, orderId: {}", loginUserId, orderId);
            return ResponseDto.success("주문을 취소했습니다.");
        }

        log.warn("주문 실패 userId: {}, orderId: {}", loginUser, orderId);

        throw new OrderCannotChangeStatusException("주문을 거절할 권한이 없습니다.");
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
    public ResponseDto<RejectOrderResponseDto> rejectOrder(UUID orderId, Long loginUserId) {

        log.info("주문 거절 요청 userId: {}, orderId: {}", loginUserId, orderId);

        User loginUser = findUserById(loginUserId);
        Order order = findOrderById(orderId);

        if (isOwner(loginUser)) {
            for (Store store : loginUser.getStores()) {
                if (store.getOrders().contains(order)) {
                    order.rejectOrder(loginUser);
                    log.info("주문 거절 성공 userId: {}, orderId: {}", loginUserId, orderId);
                    return ResponseDto.success("주문을 거절했습니다.", new RejectOrderResponseDto(orderId));
                }
            }
        }
        log.warn("주문 실패 userId: {}, orderId: {}", loginUser, orderId);
        throw new OrderCannotChangeStatusException("주문 거절 권한이 없습니다.");
    }

    private Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("주문 내역을 찾을 수 없습니다."));
    }

    /*
     *  주문 조회
     */
    @Transactional(readOnly = true)
    public ResponseDto<Page<GetOrderResponseDto>> getOrders(Long userId, UUID storeId, Long loginUserId, OrderType type, OrderStatus status, OrderPaymentStatus orderPaymentStatus, String show, Pageable pageable) {

        log.info("주문 조회 요청 loginUserId: {}, userId: {}, storeId", loginUserId, userId, storeId);

        User loginUser= findUserById(loginUserId);

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
        return ResponseDto.success("주문 데이터를 조회했습니다.", page.map(GetOrderResponseDto::new));

    }

    /*
     *  단 건 주문 조회
     */
    @Transactional(readOnly = true)
    public ResponseDto<GetOrderResponseDto> getOrder(Long loginUserId, UUID orderId) {

        log.info("주문 조회 요청 loginUserId: {}, orderId: {}", loginUserId, orderId);

        User loginUser = findUserById(loginUserId);
        Order order = findOrderById(orderId);

        if (isCustomer(loginUser) && !loginUser.getOrders().contains(order)) {
            throw new UserNotHasPermissionException("본인의 주문 내역만 조회할 수 있습니다.");
        }

        if (isOwner(loginUser)) {
            for (Store store : loginUser.getStores()) {
                store.getOrders().contains(order);
                log.info("주문 조회 성공 loginUserId: {}, orderId: {}", loginUserId, orderId);
                return ResponseDto.success("주문 조회 성공", new GetOrderResponseDto(order));
            }

        }

        if (isMasterOrManager(loginUser)) {
            log.info("주문 조회 성공 loginUserId: {}, orderId: {}", loginUserId, orderId);
            return ResponseDto.success("주문 조회 성공", new GetOrderResponseDto(order));
        }

        log.warn("주문 조회 실패 loginUserId: {}, orderId: {}", loginUserId, orderId);

        throw new UserNotHasPermissionException("주문 조회 권한이 없습니다.");
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
                    throw new UserNotHasPermissionException("본인의 주문 목록만 조회할 수 있습니다.");
                }
            }
        }
    }

    private void verifyHasPermissionToGetStoreOrders(Long userId, UUID storeId, User loginUser) {
        if (userId == null && storeId != null) {
            if (isCustomer(loginUser)) {
                log.warn("권한 문제로 인한 가게 주문 조회 실패 loginUserId: {}", loginUser.getId());
                throw new UserNotHasPermissionException("가게 주인만 가게의 주문을 조회할 수 있습니다.");
            }

            Store store = findStoreById(storeId);

            if (isOwner(loginUser) && !loginUser.getStores().contains(store)) {
                log.warn("권한 문제로 인한 가게 주문 조회 실패 loginUserId: {}", loginUser.getId());
                throw new UserNotHasPermissionException("가게 주인만 가게의 주문을 조회할 수 있습니다.");
            }
        }
    }

    private void verifyHasPermissionToGetAllOrders(Long userId, UUID storeId, User loginUser) {
        if (userId == null && storeId == null) {
            if (!isMasterOrManager(loginUser)) {
                log.warn("권한 문제로 인한 전체 주문 조회 실패 loginUserId: {}", loginUser.getId());
                throw new UserNotHasPermissionException("전체 주문 조회 권한이 없습니다.");
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
    public ResponseDto<AcceptOrderResponseDto> acceptOrder(UUID orderId, Long loginUserId) {

        log.info("주문 승낙 요청 loginUserId: {}, orderId: {}", loginUserId, orderId);

        User loginUser = findUserById(loginUserId);

        if (!isOwner(loginUser)) {
            log.warn("권한 문제로 인한(가게 주인만 가능) 주문 승낙 요청 실패 loginUserId: {}, orderId: {}", loginUserId, orderId);
            throw new OrderCannotChangeStatusException("가게 주인만 주문을 수락할 수 있습니다.");
        }

        Order order = findOrderById(orderId);

        if (!isOrderBelongsToUserStores(loginUser, order)) {
            log.warn("권한 문제로 인한(주문 받은 가게 주인만 가능) 주문 승낙 요청 실패 loginUserId: {}, orderId: {}", loginUserId, orderId);
            throw new OrderCannotChangeStatusException("가게 주인만 주문을 수락할 수 있습니다.");
        }

        order.acceptOrder();

        log.info("주문 승낙 요청 성공 loginUserId: {}, orderId: {}", loginUserId, orderId);

        return ResponseDto.success("주문을 승낙했습니다.", new AcceptOrderResponseDto(orderId));
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
    public ResponseDto<CompleteOrderResponseDto> completeOrder(UUID orderId, Long loginUserId) {

        log.info("주문 처리 완료 요청 loginUserId: {}, orderId: {}", loginUserId, orderId);

        Order order = findOrderById(orderId);
        User loginUser = findUserById(loginUserId);

        if (isOwner(loginUser)) {
            for (Store store : loginUser.getStores()) {
                if (store.getOrders().contains(order)) {
                    order.completeOrder();
                    log.info("주문 처리 완료 요청 성공 loginUserId: {}, orderId: {}", loginUserId, orderId);
                    return ResponseDto.success("주문을 완료했습니다.", new CompleteOrderResponseDto(orderId));
                }
            }
        }

        log.warn("주문 처리 완료 요청 실패 loginUserId: {}, orderId: {}", loginUserId, orderId);

        throw new OrderCannotChangeStatusException("주문을 완료할 권한이 없습니다.");
    }

    /*
     * 주문 삭제
     */
    @Transactional
    public ResponseDto<DeleteOrderResponseDto> deleteOrder(UUID orderId, Long loginUserId) {

        log.info("주문 삭제 요청 loginUserId: {}, orderId: {}", loginUserId, orderId);

        User loginUser = findUserById(loginUserId);
        Order order = findOrderById(orderId);

        if (isCustomer(loginUser)) {
            if (!order.getUser().equals(loginUser)) {
                log.warn("권한 문제로(고객은 본인 주문만 삭제 가능) 주문 삭제 요청 실패 loginUserId: {}, orderId: {}", loginUserId, orderId);
                throw new OrderCannotChangeStatusException("본인의 주문만 삭제할 수 있습니다.");
            }

            order.delete(loginUser);
            log.info("고객 주문 삭제 요청 성공 loginUserId: {}, orderId: {}", loginUserId, orderId);
            return ResponseDto.success("주문 삭제를 성공했습니다.", new DeleteOrderResponseDto(orderId));
        }

        if (isMasterOrManager(loginUser)) {
            order.delete(loginUser);
            log.info("관리자 주문 삭제 요청 성공 loginUserId: {}, orderId: {}", loginUserId, orderId);
            return ResponseDto.success("주문 삭제를 성공했습니다.", new DeleteOrderResponseDto(orderId));
        }

        if (isOwner(loginUser) && loginUser.getStores().contains(order.getStore())) {
            order.delete(loginUser);
            log.info("가게 주인 주문 삭제 요청 성공 loginUserId: {}, orderId: {}", loginUserId, orderId);
            return ResponseDto.success("주문 삭제를 성공했습니다.", new DeleteOrderResponseDto(orderId));
        }
        log.info("권한 문제로(가게 주인은 본인 가게의 주문만 삭제 가능) 주문 삭제 요청 실패 loginUserId: {}, orderId: {}", loginUserId, orderId);
        throw new OrderCannotChangeStatusException("주문 삭제 권한이 없습니다.");
    }


}
