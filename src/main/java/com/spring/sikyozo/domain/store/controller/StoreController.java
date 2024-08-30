package com.spring.sikyozo.domain.store.controller;

import com.spring.sikyozo.domain.store.entity.dto.request.CreateStoreRequestDto;
import com.spring.sikyozo.domain.store.entity.dto.request.UpdateStoreRequestDto;
import com.spring.sikyozo.domain.store.entity.dto.response.StoreResponseDto;
import com.spring.sikyozo.domain.store.entity.dto.response.UpdateStoreResponseDto;
import com.spring.sikyozo.domain.store.service.StoreService;
import com.spring.sikyozo.global.exception.dto.ApiSuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    // 가게 생성
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<StoreResponseDto>> createStore(
            @RequestBody CreateStoreRequestDto requestDto,
            @RequestParam Long id,
            HttpServletRequest servletRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        storeService.createStore(requestDto, id)
                ));
    }

    // 가게 수정
    @PutMapping("/{storeId}")
    public ResponseEntity<ApiSuccessResponse<UpdateStoreResponseDto>> updateStore(
            @PathVariable UUID storeId,
            @RequestParam Long userId,
            @RequestBody UpdateStoreRequestDto requestDto,
            HttpServletRequest servletRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        storeService.updateStore(storeId, userId, requestDto)
                ));
    }

    // 가게 삭제
    @PutMapping("/delete/{storeId}")
    public ResponseEntity<ApiSuccessResponse<String>> deleteStore(
            @PathVariable UUID storeId,
            @RequestParam Long userId,
            HttpServletRequest servletRequest) {
        storeService.deleteStore(storeId, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        "삭제 완료 되었습니다."
                ));
    }
}