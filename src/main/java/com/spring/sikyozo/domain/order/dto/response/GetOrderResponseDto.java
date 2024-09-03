package com.spring.sikyozo.domain.order.dto.response;

import com.spring.sikyozo.domain.order.entity.Order;
import com.spring.sikyozo.domain.order.entity.OrderPaymentStatus;
import com.spring.sikyozo.domain.order.entity.OrderStatus;
import com.spring.sikyozo.domain.order.entity.OrderType;
import com.spring.sikyozo.domain.ordermenu.dto.OrderMenuDto;
import com.spring.sikyozo.domain.ordermenu.entity.OrderMenu;
import com.spring.sikyozo.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class GetOrderResponseDto {

    private UUID orderId;
    private String username;
    private String storeName;
    private OrderType orderType;
    private List<OrderMenuDto> orderMenuList = new ArrayList<>();
    private Long totalPrice;
    private OrderStatus orderStatus;
    private OrderPaymentStatus orderPaymentStatus;
    private User canceledBy;
    private User deletedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime canceledAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime completedAt;
    private LocalDateTime deletedAt;



    public GetOrderResponseDto(Order order) {
        this.orderId = order.getId();
        this.username = order.getUser().getUsername();
        this.storeName = order.getStore().getStoreName();
        this.orderType = order.getType();
        this.totalPrice = order.getTotalPrice();
        this.orderStatus = order.getStatus();
        this.orderPaymentStatus = order.getOrderPaymentStatus();
        for (OrderMenu orderMenu : order.getOrderMenu()) {
            this.orderMenuList.add(new OrderMenuDto(orderMenu));
        }
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
        this.acceptedAt = order.getAcceptedAt();
        this.canceledAt = order.getCanceledAt();
        this.rejectedAt = order.getRejectedAt();
        this.canceledBy = order.getCanceledBy();
        this.deletedBy = order.getDeletedBy();
        this.deletedAt = order.getDeletedAt();

    }

}
