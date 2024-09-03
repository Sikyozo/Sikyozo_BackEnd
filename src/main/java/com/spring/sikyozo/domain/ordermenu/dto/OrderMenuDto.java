package com.spring.sikyozo.domain.ordermenu.dto;

import com.spring.sikyozo.domain.cart.entity.CartItem;
import com.spring.sikyozo.domain.order.entity.Order;
import com.spring.sikyozo.domain.ordermenu.entity.OrderMenu;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderMenuDto {

    private UUID orderMenuId;
    private String menuName;
    private int quantity;
    private Long price;

    public OrderMenuDto(OrderMenu orderMenu) {

        orderMenuId = orderMenu.getId();
        menuName = orderMenu.getMenu().getMenuName();
        quantity = orderMenu.getQuantity();
        price = orderMenu.getPrice();
    }
}
