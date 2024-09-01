package com.spring.sikyozo.domain.payment.entity;

import com.spring.sikyozo.domain.order.entity.Order;
import com.spring.sikyozo.domain.payment.exception.*;
import com.spring.sikyozo.domain.store.entity.Store;
import com.spring.sikyozo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_payments")
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(nullable = false)
    private Long price;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order order;

    // 생성, 수정, 삭제 시간
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    private LocalDateTime completedAt;

    private LocalDateTime canceledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canceled_by")
    private User canceledBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    public void addOrder(Order order) {
        if (this.order != order) {
            this.order = order;
            order.addPayment(this);
        }
    }

    public void addUser(User user) {
        if (this.user != user) {
            this.user = user;
            user.addPayment(this);
        }
    }

    public void addStore(Store store) {
        if (this.store != store) {
            this.store = store;
            store.addPayment(this);
        }
    }

    public static Payment create(User user, Order order , Long price) {
        if (!price.equals(order.getTotalPrice())) {
            throw new PaymentAmoutMismatchException();
        }

        Payment payment = new Payment();
        payment.addUser(user);
        payment.addStore(order.getStore());
        payment.order = order;

        payment.price = order.getTotalPrice();
        return payment;
    }


    public void processPayment(PaymentType type, Long price) {
        if (status.equals(PaymentStatus.COMPLETED)) {
            throw new PaymentAlreadyCompletedException();
        }

        if (status.equals(PaymentStatus.FAILED)) {
            throw new PaymentAlreadyFailedException();
        }

        if (!this.price.equals(price)) {
            throw new PaymentAmoutMismatchException();
        }

        // 결제 로직 추가
        status = PaymentStatus.COMPLETED;
        this.type = type;
        completedAt = LocalDateTime.now();
        order.addPayment(this);
    }

    public void cancel(User loginUser) {
        if (status.equals(PaymentStatus.CANCELED)) {
            throw new PaymentAlreadyCanceledException();
        }
        status = PaymentStatus.CANCELED;
        canceledBy = loginUser;
        order.cancelOrderByPayment(loginUser);

        canceledAt = LocalDateTime.now();
    }

    public void delete(User loginUser) {
        if (deletedBy != null) {
            throw new PaymentAlreadyDeletedException();
        }

        deletedBy = loginUser;
        deletedAt = LocalDateTime.now();
    }

}
