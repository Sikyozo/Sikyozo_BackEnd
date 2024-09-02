package com.spring.sikyozo.domain.region.entity;

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
@Table(name = "p_regions")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String regionName;

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

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.deletedAt == null)
            this.updatedAt = LocalDateTime.now();
    }

    // created_by
    public void createdBy(User currentUser) {
        this.createdBy = currentUser;
    }

    // updated_by
    public void updatedBy(User currentUser) {
        this.updatedBy = currentUser;
    }

    // deleted_by
    public void deletedBy(User currentUser) {
        this.deletedBy = currentUser;
    }

    // 지역 업데이트
    public void updateRegion(String regionName) {
        this.regionName = regionName;
    }

    // 지역 삭제
    public void deleteRegion() {
        this.deletedAt = LocalDateTime.now();
    }
}
