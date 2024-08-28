package com.spring.sikyozo.domain.cart.entity;

import com.spring.sikyozo.domain.menu.entity.Menu;
import lombok.*;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CartItem implements Serializable {

    private String id;
    private String name;
    private Integer quantity;
    private Long price;
    private String image;
    private String storeId;

    public static CartItem create(Menu menu, Integer quantity) {
        CartItem cartItem = new CartItem();
        cartItem.id = menu.getId().toString();
        cartItem.name = menu.getMenuName();
        cartItem.quantity = quantity;
        cartItem.price = menu.getPrice();
        cartItem.image = menu.getMenuImg();
        cartItem.storeId = menu.getStore().getId().toString();
        return cartItem;
    }
    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
