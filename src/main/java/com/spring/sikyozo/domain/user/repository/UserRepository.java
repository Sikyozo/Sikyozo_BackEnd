package com.spring.sikyozo.domain.user.repository;

import com.spring.sikyozo.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndDeletedAtIsNull(Long id);
    Page<User> findByUsernameContainingAndDeletedAtIsNull(String username, Pageable pageable);
    Page<User> findAllByDeletedAtIsNull(Pageable pageable);
    Optional<User> findByUsernameAndDeletedAtIsNull(String username);
}
