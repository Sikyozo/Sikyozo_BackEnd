package com.spring.sikyozo.domain.menu.repository;

import com.spring.sikyozo.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuRepositoryImpl extends JpaRepository<Menu, UUID> {
}
