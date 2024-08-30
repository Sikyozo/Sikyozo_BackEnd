package com.spring.sikyozo.domain.menu.entity;

import com.spring.sikyozo.domain.menu.entity.dto.request.CreateMenuRequestDto;
import com.spring.sikyozo.domain.menu.entity.dto.request.UpdateMenuRequestDto;
import com.spring.sikyozo.domain.ordermenu.entity.OrderMenu;
import com.spring.sikyozo.domain.store.entity.Store;
import com.spring.sikyozo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_menus")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false, length = 100)
    private String menuName;

    @Column(nullable = false)
    private Long price;

    @Column(columnDefinition = "text")
    private String menuImg;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
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

    // 숨김 처리
    private boolean hidden = false;

    @OneToMany(mappedBy = "menu")
    private List<OrderMenu> orderMenus = new ArrayList<>();

    @OneToMany(mappedBy = "menu")
    private List<Store> stores = new ArrayList<>();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void createMenu(CreateMenuRequestDto requestDto, User user, Store store) {
        this.store = store;
        this.menuName = requestDto.getMenuName();
        this.price = requestDto.getPrice();
        this.menuImg = requestDto.getMenuImg();
        this.createdBy = user;
    }

    public void updateMenu(UpdateMenuRequestDto requestDto, User user) {
        this.menuName = requestDto.getMenuName();
        this.price = requestDto.getPrice();
        this.menuImg = requestDto.getMenuImg();
        this.updatedBy = user;
    }

    public void deleteMenu(User user) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = user;
    }

    public void hideMenu(Menu menu) {
        this.hidden = true;
    }

    public void unHideMenu(Menu menu) {
        this.hidden = false;
    }
}
