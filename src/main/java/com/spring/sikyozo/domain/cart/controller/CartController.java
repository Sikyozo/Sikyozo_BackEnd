package com.spring.sikyozo.domain.cart.controller;

import com.spring.sikyozo.domain.cart.dto.request.AddOrUpdateCartItemRequestDto;
import com.spring.sikyozo.domain.cart.dto.request.DeleteItemRequestDto;
import com.spring.sikyozo.domain.cart.dto.response.GetCartResponseDto;
import com.spring.sikyozo.domain.cart.dto.response.RemoveFromCartResponseDto;
import com.spring.sikyozo.domain.cart.service.CartService;
import com.spring.sikyozo.global.exception.dto.ApiSuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PutMapping
    public ResponseEntity<ApiSuccessResponse<Void>> addOrUpdateCartItem(@RequestBody @Valid AddOrUpdateCartItemRequestDto requestItemDto) {
        cartService.addOrUpdateCartItem(requestItemDto.getMenuId(), requestItemDto.getQuantity());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiSuccessResponse.of(
                        HttpStatus.CREATED,
                        "/api/carts",
                        null
                ));
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<GetCartResponseDto>>> getCart() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                                "/api/carts",
                                cartService.getCart())
                        );
    }

    @DeleteMapping
    public ResponseEntity<ApiSuccessResponse<RemoveFromCartResponseDto>> removeFromCart(@RequestBody @Valid DeleteItemRequestDto deleteItemRequestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "/api/carts",
                        cartService.removeItemFromCart(deleteItemRequestDto.getMenuId())
                ));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiSuccessResponse<Void>> clearCart() {
        cartService.clearCart();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "/api/carts/clear",
                        null
                ));
    }

}
