package com.spring.sikyozo.domain.menu.entity.dto.response;

import com.spring.sikyozo.domain.menu.entity.Menu;
import lombok.Getter;

@Getter
public class CreateMenuResponseDto {

    private String storeName;
    private String menuName;
    private Long price;
    private String menuImg;

    public CreateMenuResponseDto(Menu menu) {
        this.storeName = menu.getStore().getStoreName();
        this.menuName = menu.getMenuName();
        this.price = menu.getPrice();
        this.menuImg = menu.getMenuImg();
    }
}
