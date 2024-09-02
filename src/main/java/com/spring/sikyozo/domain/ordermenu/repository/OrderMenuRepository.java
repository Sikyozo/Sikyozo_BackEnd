package com.spring.sikyozo.domain.ordermenu.repository;

import com.spring.sikyozo.domain.ordermenu.entity.OrderMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderMenuRepository extends JpaRepository<OrderMenu, UUID> {
}
