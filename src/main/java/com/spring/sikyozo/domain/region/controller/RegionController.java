package com.spring.sikyozo.domain.region.controller;

import com.spring.sikyozo.domain.region.dto.request.RegionRequestDto;
import com.spring.sikyozo.domain.region.dto.response.MessageResponseDto;
import com.spring.sikyozo.domain.region.dto.response.RegionResponseDto;
import com.spring.sikyozo.domain.region.service.RegionService;
import com.spring.sikyozo.global.exception.dto.ApiSuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/regions")
public class RegionController {
    private final RegionService regionService;

    // 지역 생성
    @PostMapping()
    public ResponseEntity<ApiSuccessResponse<RegionResponseDto>> createRegion(
            @Valid @RequestBody RegionRequestDto dto,
            HttpServletRequest servletRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        regionService.createRegion(dto)
                ));
    }

    // 지역 전체 조회
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<Page<RegionResponseDto>>> getAllRegions(
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
                        regionService.findAllRegions(page, size, search, sortBy, sortDirection)
                ));
    }

    // 지역 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<RegionResponseDto>> updateRegion(
            @PathVariable("id") UUID id,
            @Valid @RequestBody RegionRequestDto dto,
            HttpServletRequest servletRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        regionService.updateRegion(id, dto)
                ));
    }

    // 지역 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<MessageResponseDto>> deleteRegion(
            @PathVariable("id") UUID id,
            HttpServletRequest servletRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        regionService.deleteRegion(id)
                ));
    }
}
