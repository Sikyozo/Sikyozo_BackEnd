package com.spring.sikyozo.domain.industry.controller;

import com.spring.sikyozo.domain.industry.dto.request.IndustryRequestDto;
import com.spring.sikyozo.domain.industry.dto.response.IndustryResponseDto;
import com.spring.sikyozo.domain.industry.service.IndustryService;
import com.spring.sikyozo.domain.user.dto.response.MessageResponseDto;
import com.spring.sikyozo.global.exception.dto.ApiSuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/industries")
public class IndustryController {
    private final IndustryService industryService;

    // 업종 생성
    @PostMapping()
    public ResponseEntity<ApiSuccessResponse<IndustryResponseDto>> createIndustry (
            @Valid @RequestBody IndustryRequestDto dto,
            HttpServletRequest servRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servRequest.getServletPath(),
                        industryService.createIndustry(dto)
                ));
    }

    // 업종 전체 조회
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<Page<IndustryResponseDto>>> getAllIndustries(
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
                        industryService.findAllIndustries(page, size, search, sortBy, sortDirection)
                ));
    }

    // 업종 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<IndustryResponseDto>> updateIndustry(
            @PathVariable("id") Long id,
            @Valid @RequestBody IndustryRequestDto dto,
            HttpServletRequest servletRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        industryService.updateIndustry(id, dto)
                ));
    }

    // 업종 삭제 (Soft Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<MessageResponseDto>> deleteIndustry(
            @PathVariable("id") Long id,
            HttpServletRequest servletRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        industryService.deleteIndustry(id)
                ));
    }
}
