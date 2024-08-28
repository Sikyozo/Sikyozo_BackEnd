package com.spring.sikyozo.domain.menu.entity.dto.response;

import com.spring.sikyozo.domain.menu.entity.Menu;
import lombok.Getter;

@Getter
public class GetMenusResponseDto {
    private String menuName;
    private Long price;
    private String menuImg;

    public GetMenusResponseDto(Menu menu) {
        this.menuName = menu.getMenuName();
        this.price = menu.getPrice();
        this.menuImg = menu.getMenuImg();
    }
}
