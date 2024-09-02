package com.spring.sikyozo.domain.storeindustry.repository;

import com.spring.sikyozo.domain.store.entity.Store;
import com.spring.sikyozo.domain.storeindustry.entity.StoreIndustry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface StoreIndustryRepository extends JpaRepository<StoreIndustry, UUID> {

    // deletedAt이 null인 활성화된 데이터만 조회
    @Query("SELECT si FROM StoreIndustry si WHERE si.store = :store AND si.deletedAt IS NULL")
    List<StoreIndustry> findActiveByStore(@Param("store") Store store);

}
