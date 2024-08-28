package com.spring.sikyozo.domain.cart.controller;

import com.spring.sikyozo.domain.cart.dto.request.AddOrUpdateCartItemRequestDto;
import com.spring.sikyozo.domain.cart.dto.request.DeleteItemRequestDto;
import com.spring.sikyozo.domain.cart.dto.response.GetCartResponseDto;
import com.spring.sikyozo.domain.cart.dto.response.RemoveFromCartResponseDto;
import com.spring.sikyozo.domain.cart.service.CartService;
import com.spring.sikyozo.global.dto.ResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users/{userId}/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PutMapping
    public ResponseEntity<ResponseDto<String>> addOrUpdateCartItem(@PathVariable @NotNull Long userId, @RequestBody AddOrUpdateCartItemRequestDto requestItemDto) {
        cartService.addOrUpdateCartItem(userId, requestItemDto.getMenuId(), requestItemDto.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.success("장바구니 추가가 성공적으로 진행되었습니다."));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<GetCartResponseDto>>> getCart(@PathVariable @NotNull Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.getCart(userId));

    }

    @DeleteMapping
    public ResponseEntity<ResponseDto<RemoveFromCartResponseDto>> removeFromCart(@PathVariable @NotNull Long userId, @RequestBody @Valid DeleteItemRequestDto deleteItemRequestDto) {
        UUID menuId = cartService.removeItemFromCart(userId, deleteItemRequestDto.getMenuId());
        RemoveFromCartResponseDto removeFromCartResponseDto = new RemoveFromCartResponseDto(menuId.toString());
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success("장바구니에서 메뉴가 제거되었습니다", removeFromCartResponseDto));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ResponseDto<String>> clearCart(@PathVariable @NotNull Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success("장바구니가 모두 삭제되었습니다."));
    }

}
