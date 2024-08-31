package com.spring.sikyozo.domain.store.entity.dto.response;

import com.spring.sikyozo.domain.store.entity.Store;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SearchStoreResponseDto {
    private List<StoreResponseDto> stores;

    public SearchStoreResponseDto(Page<Store> storeList) {
        this.stores = storeList.stream()
                .map(StoreResponseDto::new)
                .collect(Collectors.toList());
    }
}
