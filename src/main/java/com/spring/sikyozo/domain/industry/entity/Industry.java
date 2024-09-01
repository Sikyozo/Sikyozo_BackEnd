package com.spring.sikyozo.domain.industry.entity;

import com.spring.sikyozo.domain.storeindustry.entity.StoreIndustry;
import com.spring.sikyozo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="p_industries")
public class Industry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length=100)
    private String industryName;

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

    @OneToMany(mappedBy = "industry")
    private List<StoreIndustry> storeIndustries = new ArrayList<>();

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

    // 업종 업데이트
    public void updateIndustry(String industryName) {
        this.industryName = industryName;
    }

    // 업종 삭제
    public void deleteIndustry() {
        this.deletedAt = LocalDateTime.now();
    }
}
