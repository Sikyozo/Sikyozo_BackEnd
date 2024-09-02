package com.spring.sikyozo.domain.ai.controller;

import com.spring.sikyozo.domain.ai.entity.dto.request.AiRequestDto;
import com.spring.sikyozo.domain.ai.service.AiService;
import com.spring.sikyozo.global.exception.dto.ApiSuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    @PostMapping("/{menuId}")
    public ResponseEntity<ApiSuccessResponse<?>> createAi(
            @PathVariable UUID menuId,
            @RequestBody AiRequestDto requestDto,
            HttpServletRequest servletRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        aiService.callApi(menuId,requestDto)
                ));
    }
}
