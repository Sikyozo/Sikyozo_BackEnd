package com.spring.sikyozo.domain.ordermenu.entity;

import com.spring.sikyozo.domain.menu.entity.Menu;
import com.spring.sikyozo.domain.order.entity.Order;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="p_order_menus")
public class OrderMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private Long price;
}
