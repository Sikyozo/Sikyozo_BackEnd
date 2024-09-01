package com.spring.sikyozo.domain.address.controller;

import com.spring.sikyozo.domain.address.dto.request.AddressRequestDto;
import com.spring.sikyozo.domain.address.dto.response.AddressResponseDto;
import com.spring.sikyozo.domain.address.dto.response.MessageResponseDto;
import com.spring.sikyozo.domain.address.service.AddressService;
import com.spring.sikyozo.global.exception.dto.ApiSuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AddressController {
   private final AddressService addressService;

    // 배송지 생성
    @PostMapping("/api/addresses")
    public ResponseEntity<ApiSuccessResponse<AddressResponseDto>> createAddress(
            @Valid @RequestBody AddressRequestDto dto,
            HttpServletRequest servletRequest
            ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        addressService.createAddress(dto)
                ));
    }

    // 모든 사용자 배송지 전체 조회 (MANAGER, MASTER)
    @GetMapping("/api/addresses")
    public ResponseEntity<ApiSuccessResponse<Page<AddressResponseDto>>> getAllAddresses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            HttpServletRequest servletRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        addressService.findAllAddresses(page, size, search, sortBy, sortDirection)
                ));
    }

    // CUSTOMER 별 배송지 전체 조회 (CUSTOMER, MANAGER, MASTER)
    @GetMapping("/api/users/{userId}/addresses")
    public ResponseEntity<ApiSuccessResponse<List<AddressResponseDto>>> getAllAddressesByUserId(
            @PathVariable Long userId,
            HttpServletRequest servletRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        addressService.findAllAddressesByUserId(userId)
                ));
    }

    // 배송지 수정 (CUSTOMER, MANAGER, MASTER)
    @PutMapping("/api/users/{userId}/addresses/{id}")
    public ResponseEntity<ApiSuccessResponse<AddressResponseDto>> updateAddressesByUserId(
            @PathVariable Long userId,
            @PathVariable UUID id,
            @Valid @RequestBody AddressRequestDto dto,
            HttpServletRequest servletRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        addressService.updateAddressesByUserId(userId, id, dto)
                ));
    }

    // 배송지 삭제 (CUSTOMER, MANAGER, MASTER)
    @DeleteMapping("/api/users/{userId}/addresses/{id}")
    public ResponseEntity<ApiSuccessResponse<MessageResponseDto>> deleteAddressByUserId(
            @PathVariable Long userId,
            @PathVariable UUID id,
            HttpServletRequest servletRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        addressService.deleteAddressByUserId(userId, id)
                ));
    }
}
