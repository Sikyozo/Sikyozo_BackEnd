package com.spring.sikyozo.domain.order.entity;

import com.spring.sikyozo.domain.address.entity.Address;
import com.spring.sikyozo.domain.order.exception.*;
import com.spring.sikyozo.domain.ordermenu.entity.OrderMenu;
import com.spring.sikyozo.domain.payment.entity.Payment;
import com.spring.sikyozo.domain.store.entity.Store;
import com.spring.sikyozo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="p_orders")
@Getter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="address_id")
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_id")
    private Store store;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType type = OrderType.ONLINE;

    @Column(nullable = false)
    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderPaymentStatus orderPaymentStatus = OrderPaymentStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canceled_by")
    private User canceledBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime canceledAt;

    private LocalDateTime rejectedAt;

    private LocalDateTime completedAt;

    private LocalDateTime deletedAt;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderMenu> orderMenu = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long calculateTotalPrice() {
        Long price = 0L;
        for (OrderMenu orderMenuOne : orderMenu) {
            price += orderMenuOne.getTotalPrice();
        }
        return price;
    }

    public void addUser(User user) {
        this.user = user;
        if (!user.getOrders().contains(this)) {
            user.getOrders().add(this);  // User의 orders 리스트에 추가
        }
    }

    public void addOrderMenu(OrderMenu orderMenu) {
        if(!this.orderMenu.contains(orderMenu)) {
            this.orderMenu.add(orderMenu);
            orderMenu.addOrder(this);
            totalPrice = calculateTotalPrice();
        }
    }

    public void addPayment(Payment payment) {
        this.payment = payment;
        orderPaymentStatus = OrderPaymentStatus.COMPLETE;
        payment.addOrder(this);

    }

    public void addStore(Store store) {
        if (this.store!=store) {
            this.store = store;
            store.addOrder(this);
        }
    }

    public void addAddress(Address address) {
        if (this.address != address) {
            this.address = address;
        }
    }


    // 비대면 주문
    public static Order createOrderByOnline(User user, Address address, Store store, List<OrderMenu> orderMenus) {

        Order order = new Order();
        order.addUser(user);
        order.addAddress(address);
        order.addStore(store);
        for (OrderMenu orderMenu : orderMenus) {
            order.addOrderMenu(orderMenu);
        }
        return order;

    }

    // 대면 주문
    public static Order createOrderByOffline(User user, Store store, List<OrderMenu> orderMenus) {

        Order order = new Order();
        order.addUser(user);
        order.addStore(store);
        for (OrderMenu orderMenu : orderMenus) {
            order.addOrderMenu(orderMenu);
        }
        order.type = OrderType.OFFLINE;
        return order;
    }

    // 주문 승낙
    public void acceptOrder() {

        if (status.equals(OrderStatus.PENDING) && orderPaymentStatus.equals(OrderPaymentStatus.COMPLETE)) {
            status = OrderStatus.ACCEPTED;
            acceptedAt = LocalDateTime.now();
        } else {
            throw new OrderCannotBeAcceptedException();
        }
    }

    // 주문 거절
    public void rejectOrder(User loginUser) {
        if (!status.equals(OrderStatus.PENDING) || !orderPaymentStatus.equals(OrderPaymentStatus.COMPLETE)) {
            throw new OrderCannotBeRejectedException();
        }
        status = OrderStatus.REJECTED;
        rejectedAt = LocalDateTime.now();
        orderPaymentStatus = OrderPaymentStatus.CANCEL;
        payment.cancel(loginUser);
    }

    // 주문 취소
    public void cancelOrder(User loginUser) {

        if (status.equals(OrderStatus.COMPLETE)) {
            throw new OrderAlreadyCompletedException();
        }

        if (status.equals(OrderStatus.REJECTED)) {
            throw new OrderAlreadyRejectedException();
        }

        if (status.equals(OrderStatus.CANCELED)) {
            throw new OrderAlreadyCanceledException();
        }

        status = OrderStatus.CANCELED;
        orderPaymentStatus = OrderPaymentStatus.CANCEL;
        canceledAt = LocalDateTime.now();
        canceledBy = loginUser;
        if (payment != null) {
            payment.cancel(loginUser);
        }
    }

    public void cancelOrderByPayment(User user) {
        status = OrderStatus.CANCELED;
        orderPaymentStatus = OrderPaymentStatus.CANCEL;
        canceledAt = LocalDateTime.now();
        canceledBy = user;
    }

    public void completeOrder() {
        if (!status.equals(OrderStatus.ACCEPTED)) {
            throw new OrderCannotBeCompletedException();
        }
        status = OrderStatus.COMPLETE;
        completedAt = LocalDateTime.now();
    }

    public void delete(User loginUser) {
        if (deletedBy != null) {
            throw new OrderAlreadyDeletedException();
        }

        if (status.equals(OrderStatus.ACCEPTED)) {
            throw new AcceptedOrderCannotBeDeletedException();
        }

        deletedAt = LocalDateTime.now();
        deletedBy = loginUser;
    }

}
