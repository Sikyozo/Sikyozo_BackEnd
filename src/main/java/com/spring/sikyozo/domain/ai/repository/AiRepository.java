package com.spring.sikyozo.domain.ai.repository;

import com.spring.sikyozo.domain.ai.entity.Ai;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiRepository extends JpaRepository<Ai, UUID> {

}
