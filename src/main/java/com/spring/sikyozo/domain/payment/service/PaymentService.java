package com.spring.sikyozo.domain.payment.service;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.sikyozo.domain.order.dto.response.GetOrderResponseDto;
import com.spring.sikyozo.domain.order.entity.Order;
import com.spring.sikyozo.domain.order.exception.OrderNotFoundException;
import com.spring.sikyozo.domain.order.repository.OrderRepository;
import com.spring.sikyozo.domain.payment.dto.request.GetPaymentsRequestDto;
import com.spring.sikyozo.domain.payment.dto.response.*;
import com.spring.sikyozo.domain.payment.entity.Payment;
import com.spring.sikyozo.domain.payment.entity.PaymentStatus;
import com.spring.sikyozo.domain.payment.entity.PaymentType;
import com.spring.sikyozo.domain.payment.exception.*;
import com.spring.sikyozo.domain.payment.repository.PaymentRepository;
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


    /*
     * 결제 생성
     */
    @Transactional
    public ResponseDto<CreatePaymentRseponseDto> createPayment(UUID orderId, Long loginUserId, Long price) {
        log.info("결제 생성이 요청되었습니다. orderId: {}, userId: {}, price: {}", orderId, loginUserId, price);
        Order order = findOrderById(orderId);
        User loginUser = findUserById(loginUserId);

        if (!isManagerOrMaster(loginUser) && loginUser.getOrders().contains(order)) {
            Payment payment = Payment.create(loginUser, order, price);
            paymentRepository.save(payment);
            log.info("결제 생성 요청이 정상 처리 되었습니다.. userId: {}, orderId: {}, price: {}", orderId, loginUserId, price);
            return ResponseDto.success("결제를 생성했습니다.", new CreatePaymentRseponseDto(payment.getId(), orderId, price));
        }
        log.warn("결제 생성에 실패하였습니다. userId: {}, orderId: {}, price: {}", orderId, loginUserId, price);
        throw new CannotCreatePaymentException("결제 권한이 없습니다.");
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
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("주문 정보를 찾을 수 없습니다."));
    }

    private User findUserById(Long loginUserId) {
        return userRepository.findById(loginUserId).orElseThrow(() -> new UserNotFoundException("주문 정보를 찾을 수 없습니다."));
    }

    /*
     * 결제 진행
     */
    @Transactional
    public ResponseDto<ProcessPaymentResponseDto> processPayment(Long loginUserId, UUID paymentId, PaymentType paymentType, Long price) {
        log.info("결제 처리를 요청하였습니다. userId: {}, paymentId: {}, price: {}", loginUserId, paymentId, price);
        User loginUser = findUserById(loginUserId);
        Payment payment = findPaymentById(paymentId);

        if (!isManagerOrMaster(loginUser) && loginUser.getPayments().contains(payment)) {
            payment.processPayment(paymentType, price);
            log.info("결제를 정상적으로 처리하였습니다. erId: {}, paymentId: {}, price: {}", loginUserId, paymentId, price);
            return ResponseDto.success("결제를 성공했습니다.", new ProcessPaymentResponseDto(paymentId, price));
        }
        log.warn("결제를 처리를 실패하였습니다.. erId: {}, paymentId: {}, price: {}", loginUserId, paymentId, price);
        throw new CannotProcessPaymentException("결제 권한이 없습니다.");
    }

    private Payment findPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() -> new PaymentNotFoundException("결제 정보를 찾을 수 없습니다."));
    }

    /*
     * 결제 취소
     */
    @Transactional
    public ResponseDto<CancelPaymentResponseDto> cancelPayment(Long loginUserId, UUID paymentId) {
        log.info("결제 취소를 요청하였습니다. userId: {}, paymentId: {}", loginUserId, paymentId);
        User loginUser = findUserById(loginUserId);
        Payment payment = findPaymentById(paymentId);

        if (!isManagerOrMaster(loginUser)) {
            log.warn("결제 취소를 실패하였습니다. userId: {}, paymentId: {}", loginUserId, paymentId);
            throw new CannotCancelPaymentException("관리자만 결제를 취소할 수 있습니다.");
        }

        payment.cancel(loginUser);
        log.info("결제를 정상적으로 취소하였습니다. userId: {}, paymentId: {}", loginUserId, paymentId);
        return ResponseDto.success("결제를 취소했습니다.", new CancelPaymentResponseDto(loginUser.getId(), payment.getId(), payment.getOrder().getId(), payment.getPrice()));
    }

    /*
     * 결제 삭제
     */
    @Transactional
    public ResponseDto<DeletePaymentResponseDto> deletePayment(Long loginUserId, UUID paymentId) {
        log.info("결제 삭제를 요청하였습니다.. userId: {}, paymentId: {}", loginUserId, paymentId);

        User loginUser = findUserById(loginUserId);

        if (!isManagerOrMaster(loginUser)) {
            log.info("결제 삭제를 실패하였습니다. userId: {}, paymentId: {}", loginUserId, paymentId);
            throw new PaymentException("결제 삭제 권한이 없습니다.");
        }

        Payment payment = findPaymentById(paymentId);
        payment.delete(loginUser);
        log.info("결제 정상적으로 삭제하였습니다. userId: {}, paymentId: {}", loginUserId, paymentId);
        return ResponseDto.success("결제를 삭제했습니다.", new DeletePaymentResponseDto(paymentId));
    }

    /*
     * 결제 조회
     */
    @Transactional(readOnly = true)
    public ResponseDto<Page<GetPaymentsResponseDto>> getPayments(Long userId, UUID storeId, Long loginUserId, PaymentType type, PaymentStatus status, String show, Pageable pageable) {
        log.info("결제 목록 조회를 요청하였습니다. userId: {}, loginUserId: {}", userId, loginUserId);
        User loginUser = findUserById(loginUserId);

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
                        showEq(show)
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
                        showEq(show)
                );

        Page<Payment> page = PageableExecutionUtils.getPage(payments, pageable, countQuery::fetchOne);
        return ResponseDto.success("주문 데이터를 조회했습니다.", page.map(GetPaymentsResponseDto::new));

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

    private  BooleanExpression showEq(String hide) {
        if (hide == "all") {
            return null;
        }

        if (hide == "deleted") {
            return payment.deletedAt.isNotNull();
        }
        return payment.deletedAt.isNull();
    }


    private void verifyHasPermissionToGetUserPayments(Long userId, UUID storeId, User loginUser) {
        if (userId != null && storeId != null) {
            if (isCustomer(loginUser) || isOwner(loginUser)) {
                if (!loginUser.getId().equals(userId)) {
                    log.warn("단일 유저 결제 목록 조회 검증에 실패했습니다. loginUserId: {}, userId: {}, storeId: {}", loginUser.getId(), userId, storeId);
                    throw new UserNotHasPermissionException("본인의 결제 목록만 조회할 수 있습니다.");
                }
            }
        }
    }

    private void verifyHasPermissionToGetStorePayments(Long userId, UUID storeId, User loginUser) {
        if (userId == null && storeId != null) {
            if (isCustomer(loginUser)) {
                log.warn("가게 결제 목록 조회에 실패했습니다. loginUserId: {}, userId: {}, storeId: {}", loginUser.getId(), userId, storeId);
                throw new UserNotHasPermissionException("가게 주인만 가게의 결제를 조회할 수 있습니다.");
            }

            Store store = findStoreById(storeId);

            if (isOwner(loginUser) && !loginUser.getStores().contains(store)) {
                log.warn("가게 결제 목록 조회에 실패했습니다. loginUserId: {}, userId: {}, storeId: {}", loginUser.getId(), userId, storeId);
                throw new UserNotHasPermissionException("가게 주인만 가게의 결제를 조회할 수 있습니다.");
            }
        }
    }

    private void verifyHasPermissionToGetAllPayments(Long userId, UUID storeId, User loginUser) {
        if (userId == null && storeId == null) {
            if (!isManagerOrMaster(loginUser)) {
                log.warn("전체 결제 목록 조회에 실패했습니다. loginUserId: {}, userId: {}, storeId: {}", loginUser.getId(), userId, storeId);
                throw new UserNotHasPermissionException("전체 결제 조회 권한이 없습니다.");
            }
        }
    }

    /*
     *  단 건 결제 조회
     */
    @Transactional(readOnly = true)
    public ResponseDto<GetPaymentsResponseDto> getPayment(Long loginUserId, UUID paymentId) {

        log.info("결제 조회 요청 loginUserId: {}, orderId: {}", loginUserId, paymentId);

        User loginUser = findUserById(loginUserId);
        Payment payment = findPaymentById(paymentId);

        if (isCustomer(loginUser) && !loginUser.getOrders().contains(payment)) {
            throw new UserNotHasPermissionException("본인의 결제 내역만 조회할 수 있습니다.");
        }

        if (isOwner(loginUser)) {
            for (Store store : loginUser.getStores()) {
                store.getPayments().contains(payment);
                log.info("결제 조회 성공 loginUserId: {}, paymentId: {}", loginUserId, paymentId);
                return ResponseDto.success("결제 조회 성공", new GetPaymentsResponseDto(payment));
            }

        }

        if (isManagerOrMaster(loginUser)) {
            log.info("결제 조회 성공 loginUserId: {}, orderId: {}", loginUserId, paymentId);
            return ResponseDto.success("결제 조회 성공", new GetPaymentsResponseDto(payment));
        }

        log.warn("결제 조회 실패 loginUserId: {}, paymentId: {}", loginUserId, paymentId);

        throw new UserNotHasPermissionException("결제 조회 권한이 없습니다.");
    }

    private Store findStoreById(UUID storeId) {
        return storeRepository.findById(storeId).orElseThrow(() -> new StoreNotFoundException("존재하지 않는 가게입니다."));
    }

}


