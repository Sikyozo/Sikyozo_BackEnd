package com.spring.sikyozo.domain.store.entity.dto.response;

import com.spring.sikyozo.domain.store.entity.Store;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SearchStoreResponseDto {

    private List<String> storeName;

    public SearchStoreResponseDto(Page<Store> storeList) {
        this.storeName = storeList.stream()
                .map(Store::getStoreName)
                .collect(Collectors.toList());
    }
}
