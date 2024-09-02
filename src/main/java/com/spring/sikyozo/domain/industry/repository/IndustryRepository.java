package com.spring.sikyozo.domain.industry.repository;

import com.spring.sikyozo.domain.industry.entity.Industry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IndustryRepository extends JpaRepository<Industry, UUID> {
    Optional<Industry> findByIndustryNameAndDeletedAtIsNull(String industryName);
    Page<Industry> findByIndustryNameContainingAndDeletedAtIsNull(String search, Pageable pageable);
    Page<Industry> findAllByDeletedAtIsNull(Pageable pageable);
    Optional<Industry> findByIdAndDeletedAtIsNull(UUID id);

    @Query("SELECT i FROM Industry i WHERE i.industryName IN :industryNames")
    List<Industry> findByIndustryNameIn(@Param("industryNames") List<String> industryNames);

}
