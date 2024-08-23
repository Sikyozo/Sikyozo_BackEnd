package com.spring.sikyozo.domain.region.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "p_regions")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String regionName;


}
