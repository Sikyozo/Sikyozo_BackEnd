package com.spring.sikyozo.domain.store.repository;

import com.spring.sikyozo.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {

//    List<Store> findByStore_
}
