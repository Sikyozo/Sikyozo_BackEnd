package com.spring.sikyozo.domain.address.entity;

import com.spring.sikyozo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "p_addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String addressName;

    @Column(length=100)
    private String request;

    // 생성, 수정, 삭제 시간
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.deletedAt == null)
            this.updatedAt = LocalDateTime.now();
    }

    // updated_by
    public void updatedBy(User currentUser) {
        this.updatedBy = currentUser;
    }

    // deleted_by
    public void deletedBy(User currentUser) {
        this.deletedBy = currentUser;
    }

    // 배송지 및 요청 사항 업데이트
    public void updateAddress(String addressName) {
        this.addressName = addressName;
    }

    // 요청 사항 업데이트
    public void updateRequest(String request) {
        this.request = request;
    }

    // 배송지 삭제
    public void deleteAddress() {
        this.deletedAt = LocalDateTime.now();
    }
}
