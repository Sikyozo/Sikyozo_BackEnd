package com.spring.sikyozo.domain.industry.repository;

import com.spring.sikyozo.domain.industry.entity.Industry;
import com.spring.sikyozo.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IndustryRepository extends JpaRepository<Industry, UUID> {

    Optional<Industry> findByIndustryName(String industryName);

    List<Industry> findByStoreIdAndDeletedAtIsNull(UUID storeId);

    List<Industry> findByStore(Store store);
}
