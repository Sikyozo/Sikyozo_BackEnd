package com.spring.sikyozo.domain.region.repository;

import com.spring.sikyozo.domain.region.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegionRepository extends JpaRepository<Region, UUID> {
    Optional<Region> findByRegionName(String regionName);
    Page<Region> findByRegionNameContainingAndDeletedAtIsNull(String search, Pageable pageable);
    Page<Region> findAllByDeletedAtIsNull(Pageable pageable);
    Optional<Region> findByIdAndDeletedAtIsNull(UUID id);
}
