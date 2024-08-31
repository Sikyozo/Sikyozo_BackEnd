package com.spring.sikyozo.domain.store.repository;

import com.spring.sikyozo.domain.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {

    @Query("SELECT s FROM Store s " +
            "LEFT JOIN s.menu m " +
            "JOIN s.storeIndustries si " +
            "JOIN si.industry i " +
            "WHERE (:menuName IS NULL OR m.menuName LIKE %:menuName%) " +
            "AND i.industryName LIKE %:industryName% " +
            "AND s.deletedAt IS NULL " +
            "ORDER BY s.updatedAt DESC")
    Page<Store> findByMenuNameAndIndustryName(@Param("menuName") String menuName,
                                              @Param("industryName") String industryName,
                                              Pageable pageable);


}
