package com.spring.sikyozo.domain.store.entity;

import com.spring.sikyozo.domain.industry.entity.Industry;
import com.spring.sikyozo.domain.order.entity.Order;
import com.spring.sikyozo.domain.menu.entity.Menu;
import com.spring.sikyozo.domain.payment.entity.Payment;
import com.spring.sikyozo.domain.region.entity.Region;
import com.spring.sikyozo.domain.store.entity.dto.request.CreateStoreRequestDto;
import com.spring.sikyozo.domain.store.entity.dto.request.UpdateStoreRequestDto;
import com.spring.sikyozo.domain.storeindustry.entity.StoreIndustry;
import com.spring.sikyozo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_stores")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "region_id")
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @Column(nullable = false, length = 100)
    private String storeName;

    @Column(columnDefinition = "text")
    private String storeImg;

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

    @OneToMany(mappedBy = "store")
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<StoreIndustry> storeIndustries = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<Order> orders = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addPayment(Payment payment) {
        if (this.payments != payment) {
            this.payments = payments;
            payment.addStore(this);
        }
    }

    public void addOrder(Order order) {
        if (!this.orders.contains(order)) {
            this.orders.add(order);
            order.addStore(this);
        }
    }

    public void removeOrder(Order order) {
            this.orders.remove(order);
    }

    // 가게 생성
    public void createStore(CreateStoreRequestDto requestDto, User user, Region region) {
        this.user = user;
        this.region = region;
        this.storeName = requestDto.getStoreName();
        this.storeImg = requestDto.getStoreImg();
    }

    //가게 업종 추가
    public void setStoreIndustries(List<StoreIndustry> industries) {
        this.storeIndustries = industries;
    }

    // 가게 수정
    public void updateStore(UpdateStoreRequestDto requestDto, User user, Region region, List<Industry> updateIndustries) {
        this.updatedBy = user;
        this.region = region;
        this.storeName = requestDto.getStoreName();
        this.storeImg = requestDto.getStoreImg();
        this.updatedAt = LocalDateTime.now();
//        this.industries = updateIndustries;
    }

    public void deleteStore(User user) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = user;
    }

}
