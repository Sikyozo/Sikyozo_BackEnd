package com.spring.sikyozo.domain.menu.repository;

import com.spring.sikyozo.domain.menu.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
    List<Menu> findByHiddenFalse();

    Page<Menu> findByStore_StoreNameContainingAndMenuNameContainingAndHiddenFalse(String menuName, String StoreName, Pageable pageable);
}
