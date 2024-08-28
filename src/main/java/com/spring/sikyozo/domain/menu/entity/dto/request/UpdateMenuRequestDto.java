package com.spring.sikyozo.domain.menu.entity.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateMenuRequestDto {

    private String menuName;
    private Long price;
    private String menuImg;

}
