package com.spring.sikyozo.domain.address.repository;

import com.spring.sikyozo.domain.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
}
