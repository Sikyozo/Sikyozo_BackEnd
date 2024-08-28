package com.spring.sikyozo.domain.cart.dto.response;

import com.spring.sikyozo.domain.cart.entity.CartItem;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCartResponseDto {

    private String id;
    private CartItem cartItem;

}
