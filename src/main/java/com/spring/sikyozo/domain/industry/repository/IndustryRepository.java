package com.spring.sikyozo.domain.industry.repository;

import com.spring.sikyozo.domain.industry.entity.Industry;
import com.spring.sikyozo.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IndustryRepository extends JpaRepository<Industry, UUID> {

    @Query("SELECT i FROM Industry i WHERE i.industryName IN :industryNames")
    List<Industry> findByIndustryNameIn(@Param("industryNames") List<String> industryNames);


//    List<Industry> findByStoreIdAndDeletedAtIsNull(UUID storeId);

//    List<Industry> findByStore(Store store);
}
