package com.spring.sikyozo.domain.menu.repository;

import com.spring.sikyozo.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
    List<Menu> findByHiddenFalse();

    List<Menu> findByStore_StoreNameContainingAndMenuNameContainingAndHiddenFalse(String menuName, String StoreName);
}
