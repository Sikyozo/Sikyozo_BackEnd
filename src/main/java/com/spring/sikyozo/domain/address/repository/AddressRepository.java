package com.spring.sikyozo.domain.address.repository;

import com.spring.sikyozo.domain.address.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    Optional<Address> findByAddressNameAndDeletedAtIsNull(String addressName);
    Page<Address> findByAddressNameContainingAndDeletedAtIsNull(String search, Pageable pageable);
    Page<Address> findAllByDeletedAtIsNull(Pageable pageable);
    List<Address> findAllByUserIdAndDeletedAtIsNull(Long userId);
    Optional<Address> findByIdAndDeletedAtIsNull(UUID id);
}
