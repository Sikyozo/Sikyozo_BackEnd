package com.spring.sikyozo.domain.store.entity.dto.response;

import com.spring.sikyozo.domain.industry.entity.Industry;
import com.spring.sikyozo.domain.store.entity.Store;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UpdateStoreResponseDto {

    private String userName;
    private String regionName;
    private String storeName;
    private String storeImg;
    private List<String> industryNames;

    public UpdateStoreResponseDto(Store store,List<Industry> industries) {
        this.userName = store.getUser().getUsername();
        this.regionName = store.getRegion().getRegionName();
        this.storeName = store.getStoreName();
        this.storeImg = store.getStoreImg();
        this.industryNames = industries.stream()
                .map(Industry::getIndustryName)
                .collect(Collectors.toList());
    }
}
