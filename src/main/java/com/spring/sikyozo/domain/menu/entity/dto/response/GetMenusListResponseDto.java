package com.spring.sikyozo.domain.menu.entity.dto.response;

import com.spring.sikyozo.domain.menu.entity.Menu;
import lombok.Getter;

@Getter
public class GetMenusListResponseDto {

    private String menuName;
    private String storeName;

    public GetMenusListResponseDto(Menu menu) {
        this.menuName = menu.getMenuName();
        this.storeName = menu.getStore().getStoreName();
    }
}
