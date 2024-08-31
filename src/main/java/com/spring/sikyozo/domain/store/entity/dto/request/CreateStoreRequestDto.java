package com.spring.sikyozo.domain.store.entity.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class CreateStoreRequestDto {

    private String regionName;
    private String storeName;
    private String storeImg;
    private List<String> industryNames;

}
