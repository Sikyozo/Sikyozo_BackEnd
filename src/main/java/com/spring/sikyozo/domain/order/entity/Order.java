package com.spring.sikyozo.domain.order.entity;

import com.spring.sikyozo.domain.address.entity.Address;
import com.spring.sikyozo.domain.ordermenu.entity.OrderMenu;
import com.spring.sikyozo.domain.payment.entity.OrderStatus;
import com.spring.sikyozo.domain.payment.entity.Payment;
import com.spring.sikyozo.domain.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="p_orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="address_id", nullable = false)
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType type = OrderType.ONLINE;

    @Column(nullable = false)
    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime canceledAt;

    private LocalDateTime rejectedAt;


    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private Payment payment;

    @OneToMany(mappedBy = "order")
    private List<OrderMenu> orderMenu;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
