package com.spring.sikyozo.domain.storeindustry.repository;

import com.spring.sikyozo.domain.storeindustry.entity.StoreIndustry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoreIndustryRepository extends JpaRepository<StoreIndustry, UUID> {

}
