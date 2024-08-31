package com.spring.sikyozo.domain.storeindustry.entity;

import com.spring.sikyozo.domain.industry.entity.Industry;
import com.spring.sikyozo.domain.store.entity.Store;
import com.spring.sikyozo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_store_industries")
public class StoreIndustry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_id", nullable = false)
    private Industry industry;

    // 생성, 수정, 삭제 시간
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", updatable = false)
    private User createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void createStoreIndustry(User user, Store store, Industry industry) {
        this.createdBy = user;
        this.store = store;
        this.industry = industry;
    }

    public void deleteStoreIndustry(User user) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = user;
    }

    public void setDeletedAt(Object o) {
        this.deletedAt = null;
    }
}
