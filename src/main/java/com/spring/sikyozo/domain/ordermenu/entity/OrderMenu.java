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

    public void addOrder(Order order) {
        if (this.order!=order) {
            this.order = order;
            order.addOrderMenu(this);
        }
    }

    public static OrderMenu create(Menu menu, int quantity) {
        OrderMenu orderMenu = new OrderMenu();
        orderMenu.menu = menu;
        orderMenu.price = menu.getPrice();
        orderMenu.quantity = quantity;
        return orderMenu;
    }

    public Long getTotalPrice() {
        return quantity * price;
    }
}
