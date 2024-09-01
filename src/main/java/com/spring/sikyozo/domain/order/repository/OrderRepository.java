package com.spring.sikyozo.domain.order.repository;

import com.spring.sikyozo.domain.order.entity.Order;
import com.spring.sikyozo.domain.store.entity.Store;
import com.spring.sikyozo.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface OrderRepository  extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    Page<Order> findAll(Pageable pageable);

    Page<Order> findByStore(Store store, Pageable pageable);

    Page<Order> findByUser(User user, Pageable pageable);
}
