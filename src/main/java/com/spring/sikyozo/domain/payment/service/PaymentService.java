package com.spring.sikyozo.domain.payment.service;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.sikyozo.domain.order.entity.Order;
import com.spring.sikyozo.domain.order.exception.OrderNotFoundException;
import com.spring.sikyozo.domain.order.repository.OrderRepository;
import com.spring.sikyozo.domain.payment.dto.response.*;
import com.spring.sikyozo.domain.payment.entity.Payment;
import com.spring.sikyozo.domain.payment.entity.PaymentStatus;
import com.spring.sikyozo.domain.payment.entity.PaymentType;
import com.spring.sikyozo.domain.payment.exception.PaymentNotFoundException;
import com.spring.sikyozo.domain.payment.repository.PaymentRepository;
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

import java.util.List;
import java.util.UUID;

import static com.spring.sikyozo.domain.payment.entity.QPayment.payment;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final SecurityUtil securityUtil;


    /*
     * 결제 생성
     */
    @Transactional
    public CreatePaymentRseponseDto createPayment(UUID orderId, Long price) {

        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();
        Order order = findOrderById(orderId);

        log.info("결제 생성이 요청되었습니다. orderId: {}, userId: {}, price: {}", orderId, loginUserId, price);

        if (!isManagerOrMaster(loginUser) && loginUser.getOrders().contains(order)) {
            Payment payment = Payment.create(loginUser, order, price);
            paymentRepository.save(payment);
            log.info("결제 생성 요청이 정상 처리 되었습니다.. userId: {}, orderId: {}, price: {}", orderId, loginUserId, price);
            return new CreatePaymentRseponseDto(payment.getId(), orderId, price);
        }
        log.warn("결제 생성에 실패하였습니다. userId: {}, orderId: {}, price: {}", orderId, loginUserId, price);
        throw new AccessDeniedException();
    }


    private boolean isCustomer(User loginUser) {
        return loginUser.getRole().equals(UserRole.CUSTOMER);
    }

    private boolean isOwner(User loginUser) {
        return loginUser.getRole().equals(UserRole.OWNER);
    }

    private boolean isManagerOrMaster(User loginUser) {
        return (loginUser.getRole().equals(UserRole.MASTER) || loginUser.getRole().equals(UserRole.MANAGER));
    }


    private Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
    }

    private User findUserById(Long loginUserId) {
        return userRepository.findById(loginUserId).orElseThrow(UserNotFoundException::new);
    }

    /*
     * 결제 진행
     */
    @Transactional
    public ProcessPaymentResponseDto processPayment(UUID paymentId, PaymentType paymentType, Long price) {

        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();
        Payment payment = findPaymentById(paymentId);

        log.info("결제 처리를 요청하였습니다. userId: {}, paymentId: {}, price: {}", loginUserId, paymentId, price);

        if (!isManagerOrMaster(loginUser) && loginUser.getPayments().contains(payment)) {
            payment.processPayment(paymentType, price);
            log.info("결제를 정상적으로 처리하였습니다. erId: {}, paymentId: {}, price: {}", loginUserId, paymentId, price);
            return new ProcessPaymentResponseDto(paymentId, price);
        }
        log.warn("결제를 처리를 실패하였습니다.. erId: {}, paymentId: {}, price: {}", loginUserId, paymentId, price);
        throw new AccessDeniedException();
    }

    private Payment findPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(PaymentNotFoundException::new);
    }

    /*
     * 결제 취소
     */
    @Transactional
    public CancelPaymentResponseDto cancelPayment(UUID paymentId) {

        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();
        Payment payment = findPaymentById(paymentId);

        log.info("결제 취소를 요청하였습니다. userId: {}, paymentId: {}", loginUserId, paymentId);

        if (!isManagerOrMaster(loginUser)) {
            log.warn("결제 취소를 실패하였습니다. userId: {}, paymentId: {}", loginUserId, paymentId);
            throw new AccessDeniedException();
        }

        payment.cancel(loginUser);
        log.info("결제를 정상적으로 취소하였습니다. userId: {}, paymentId: {}", loginUserId, paymentId);
        return new CancelPaymentResponseDto(loginUser.getId(), payment.getId(), payment.getOrder().getId(), payment.getPrice());
    }

    /*
     * 결제 삭제
     */
    @Transactional
    public DeletePaymentResponseDto deletePayment(UUID paymentId) {

        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();

        log.info("결제 삭제를 요청하였습니다.. userId: {}, paymentId: {}", loginUserId, paymentId);

        if (!isManagerOrMaster(loginUser)) {
            log.info("결제 삭제를 실패하였습니다. userId: {}, paymentId: {}", loginUserId, paymentId);
            throw new AccessDeniedException();
        }

        Payment payment = findPaymentById(paymentId);
        payment.delete(loginUser);
        log.info("결제 정상적으로 삭제하였습니다. userId: {}, paymentId: {}", loginUserId, paymentId);
        return new DeletePaymentResponseDto(paymentId);
    }

    /*
     * 결제 조회
     */
    @Transactional(readOnly = true)
    public Page<GetPaymentsResponseDto> getPayments(Long userId, UUID storeId, PaymentType type, PaymentStatus status, String show, Pageable pageable) {

        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();

        log.info("결제 목록 조회를 요청하였습니다. userId: {}, loginUserId: {}", userId, loginUserId);

        verifyHasPermissionToGetAllPayments(userId, storeId, loginUser);
        verifyHasPermissionToGetStorePayments(userId, storeId, loginUser);
        verifyHasPermissionToGetUserPayments(userId, storeId, loginUser);

        List<Payment> payments = jpaQueryFactory
                .selectFrom(payment)
                .where(
                        userEq(userId),
                        storeEq(storeId),
                        typeEq(type),
                        statusEq(status),
                        showEq(show, loginUser)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSortOrder(pageable))
                .fetch();


        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(payment.count())
                .where(
                        userEq(userId),
                        storeEq(storeId),
                        typeEq(type),
                        statusEq(status),
                        showEq(show, loginUser)
                );

        Page<Payment> page = PageableExecutionUtils.getPage(payments, pageable, countQuery::fetchOne);
        return page.map(GetPaymentsResponseDto::new);

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
        return (userId != null) ? payment.user.eq(findUserById(userId)) : null;
    }

    private  BooleanExpression storeEq(UUID storeId) {
        return (storeId != null) ? (payment.store.eq(findStoreById(storeId))) : null;
    }

    private  BooleanExpression statusEq(PaymentStatus status) {
        return (status != null) ? (payment.status.eq(status)) : null;
    }

    private  BooleanExpression typeEq(PaymentType type) {
        return (type != null) ? (payment.type.eq(type)) : null;
    }

    // all : 삭제, 미삭제 모두 조회
    // deleted : 삭제 된 것만
    // null : 미삭제만 조회
    private  BooleanExpression showEq(String show, User loginUser) {

        // Customer or Owner
        if (isOwner(loginUser) || isCustomer(loginUser)) {
            return payment.deletedBy.isNull()
                    .or(payment.deletedBy.ne(loginUser)
                    .and(payment.deletedBy.role.ne(UserRole.MASTER)
                    .and(payment.deletedBy.role.ne(UserRole.MANAGER))));
        }

        // 관리자만
        if (show == "all") {
            return null;
        }

        // 관리자만
        if (show == "deleted") {
            return payment.deletedAt.isNotNull();
        }

        //
        return payment.deletedAt.isNull();
    }


    private void verifyHasPermissionToGetUserPayments(Long userId, UUID storeId, User loginUser) {
        if (userId != null && storeId != null) {
            if (isCustomer(loginUser) || isOwner(loginUser)) {
                if (!loginUser.getId().equals(userId)) {
                    log.warn("단일 유저 결제 목록 조회 검증에 실패했습니다. loginUserId: {}, userId: {}, storeId: {}", loginUser.getId(), userId, storeId);
                    throw new AccessDeniedException();
                }
            }
        }
    }

    private void verifyHasPermissionToGetStorePayments(Long userId, UUID storeId, User loginUser) {
        if (userId == null && storeId != null) {
            if (isCustomer(loginUser)) {
                log.warn("가게 결제 목록 조회에 실패했습니다. loginUserId: {}, userId: {}, storeId: {}", loginUser.getId(), userId, storeId);
                throw new AccessDeniedException();
            }

            Store store = findStoreById(storeId);

            if (isOwner(loginUser) && !loginUser.getStores().contains(store)) {
                log.warn("가게 결제 목록 조회에 실패했습니다. loginUserId: {}, userId: {}, storeId: {}", loginUser.getId(), userId, storeId);
                throw new AccessDeniedException();
            }
        }
    }

    private void verifyHasPermissionToGetAllPayments(Long userId, UUID storeId, User loginUser) {
        if (userId == null && storeId == null) {
            if (!isManagerOrMaster(loginUser)) {
                log.warn("전체 결제 목록 조회에 실패했습니다. loginUserId: {}, userId: {}, storeId: {}", loginUser.getId(), userId, storeId);
                throw new AccessDeniedException();
            }
        }
    }

    /*
     *  단 건 결제 조회
     */
    @Transactional(readOnly = true)
    public GetPaymentsResponseDto getPayment(UUID paymentId) {


        User loginUser = securityUtil.getCurrentUser();
        Long loginUserId = loginUser.getId();
        Payment payment = findPaymentById(paymentId);

        log.info("결제 조회 요청 loginUserId: {}, orderId: {}", loginUserId, paymentId);

        if (isCustomer(loginUser) && !loginUser.getOrders().contains(payment)) {
            throw new AccessDeniedException();
        }

        if (isOwner(loginUser)) {
            for (Store store : loginUser.getStores()) {
                store.getPayments().contains(payment);
                log.info("결제 조회 성공 loginUserId: {}, paymentId: {}", loginUserId, paymentId);
                return new GetPaymentsResponseDto(payment);
            }

        }

        if (isManagerOrMaster(loginUser)) {
            log.info("결제 조회 성공 loginUserId: {}, orderId: {}", loginUserId, paymentId);
            return new GetPaymentsResponseDto(payment);
        }

        log.warn("결제 조회 실패 loginUserId: {}, paymentId: {}", loginUserId, paymentId);

        throw new AccessDeniedException();
    }

    private Store findStoreById(UUID storeId) {
        return storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
    }

}


