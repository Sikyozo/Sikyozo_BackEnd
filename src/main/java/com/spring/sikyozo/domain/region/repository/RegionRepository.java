package com.spring.sikyozo.domain.region.repository;

import com.spring.sikyozo.domain.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

public interface RegionRepository extends JpaRepository<Region, UUID> {

    Optional<Region> findByRegionName(String regionName);
}
