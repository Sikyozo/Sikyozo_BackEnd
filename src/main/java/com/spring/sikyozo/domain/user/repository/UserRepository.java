package com.spring.sikyozo.domain.user.repository;

import com.spring.sikyozo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
