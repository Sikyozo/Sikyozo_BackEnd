package com.spring.sikyozo.domain.user.entity;

import com.spring.sikyozo.domain.address.entity.Address;
import com.spring.sikyozo.domain.ai.entity.Ai;
import com.spring.sikyozo.domain.notice.entity.Notice;
import com.spring.sikyozo.domain.order.entity.Order;
import com.spring.sikyozo.domain.payment.entity.Payment;
import com.spring.sikyozo.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "p_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String nickname;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 12, nullable = false)
    private UserRole role;

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

    @OneToMany(mappedBy = "user")
    private List<Store> stores = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Notice> notices = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Ai> ai = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.deletedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    // updated_by
    public void updatedBy(User currentUser) {
        this.updatedBy = currentUser;
    }

    // deleted_by
    public void deletedBy(User currentUser) {
        this.deletedBy = currentUser;
    }

    // 닉네임 업데이트
    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    // 이메일 업데이트
    public void updateEmail(String newEmail) {
        this.email = newEmail;
    }

    // 비밀번호 업데이트
    public void updatePassword(String encodedNewPassword) {
        this.password = encodedNewPassword;
    }

    // 사용자 탈퇴 (Soft Delete)
    public void deleteUser() {
        this.deletedAt = LocalDateTime.now();
    }
}
