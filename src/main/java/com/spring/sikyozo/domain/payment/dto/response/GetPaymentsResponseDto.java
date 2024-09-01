package com.spring.sikyozo.domain.payment.dto.response;

import com.spring.sikyozo.domain.payment.entity.Payment;
import com.spring.sikyozo.domain.payment.entity.PaymentType;
import com.spring.sikyozo.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class GetPaymentsResponseDto {
    private UUID orderId;
    private String username;
    private String storeName;
    private PaymentType paymentType;
    private Long totalPrice;
    private User canceledBy;
    private User deletedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime canceledAt;
    private LocalDateTime completedAt;
    private LocalDateTime deletedAt;


    public GetPaymentsResponseDto(Payment payment) {
        this.orderId = payment.getId();
        this.username = payment.getUser().getUsername();
        this.storeName = payment.getStore().getStoreName();
        this.paymentType = payment.getType();
        this.totalPrice = payment.getPrice();
        this.canceledBy = payment.getCanceledBy();
        this.deletedBy = payment.getDeletedBy();
        this.createdAt = payment.getCreatedAt();
        this.updatedAt = payment.getUpdatedAt();
        this.canceledAt = payment.getCanceledAt();
        this.completedAt = payment.getCompletedAt();
        this.deletedAt = payment.getDeletedAt();
    }
}
