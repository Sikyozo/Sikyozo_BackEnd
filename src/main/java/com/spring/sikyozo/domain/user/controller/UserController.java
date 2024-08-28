package com.spring.sikyozo.domain.user.controller;

import com.spring.sikyozo.domain.user.dto.request.SignUpRequestDto;
import com.spring.sikyozo.domain.user.dto.response.MessageResponseDto;
import com.spring.sikyozo.domain.user.sevice.UserService;
import com.spring.sikyozo.global.exception.dto.ApiSuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiSuccessResponse<MessageResponseDto>> signUp (
            @Valid @RequestBody SignUpRequestDto dto,
            HttpServletRequest servRequest
    ) {
        log.info("회원가입 API 시작");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servRequest.getServletPath(),
                        userService.signUp(dto)
                ));
    }
}
