package com.spring.sikyozo.domain.industry.repository;

import com.spring.sikyozo.domain.industry.entity.Industry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IndustryRepository extends JpaRepository<Industry, Long> {
    Optional<Industry> findByIndustryName(String industryName);
    Page<Industry> findByIndustryNameContainingAndDeletedAtIsNull(String search, Pageable pageable);
    Page<Industry> findAllByDeletedAtIsNull(Pageable pageable);
    Optional<Industry> findByIdAndDeletedAtIsNull(Long id);
}
