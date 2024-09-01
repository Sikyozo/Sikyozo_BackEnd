package com.spring.sikyozo.domain.payment.controller;

import com.spring.sikyozo.domain.payment.dto.request.*;
import com.spring.sikyozo.domain.payment.dto.response.*;
import com.spring.sikyozo.domain.payment.entity.PaymentStatus;
import com.spring.sikyozo.domain.payment.entity.PaymentType;
import com.spring.sikyozo.domain.payment.service.PaymentService;
import com.spring.sikyozo.global.exception.dto.ApiSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /*
     * 결제 생성
     */

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<CreatePaymentRseponseDto>> createPayment(@RequestBody CreatePaymentRequestDto createPaymentRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiSuccessResponse.of(
                        HttpStatus.CREATED,
                        "/api/payments",
                        paymentService.createPayment(createPaymentRequestDto.getOrderId(), createPaymentRequestDto.getPrice())
                ));
    }

    /*
     * 결제 진행
     */

    @PatchMapping
    public ResponseEntity<ApiSuccessResponse<ProcessPaymentResponseDto>> processPayment(@RequestBody ProcessPaymentRequestDto processPaymentRequestDto) {
        ProcessPaymentResponseDto processPaymentResponse = paymentService.processPayment(processPaymentRequestDto.getPaymentId(), processPaymentRequestDto.getPaymentType(), processPaymentRequestDto.getPrice());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "/api/payments",
                        processPaymentResponse
                ));
    }

    /*
     * 결제 조회
     */

    /*
     * show : All, deleted, null
     */
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<Page<GetPaymentsResponseDto>>> getPayments(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) UUID storeId,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) PaymentType type,
            @RequestParam(required = false) String show,
            Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "/api/payments?userId=" + userId + "&storeId=" + storeId + "&status=" + status + "&type=" + type + "&show=" + show,
                        paymentService.getPayments(userId, storeId, type, status, show, pageable)
                ));
    }

    /*
     * 결제 취소
     */

    @DeleteMapping
    public ResponseEntity<ApiSuccessResponse<CancelPaymentResponseDto>> cancelPayment(@RequestBody CancelPaymentRequestDto cancelPaymentRequestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "/api/payments",
                        paymentService.cancelPayment(cancelPaymentRequestDto.getPaymentId())
                ));
    }

    /*
     * 결제 삭제
     */

    @DeleteMapping("/delete")
    public ResponseEntity<ApiSuccessResponse<DeletePaymentResponseDto>> deletePayment(@RequestBody DeletePaymentRequestDto deletePaymentRequestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "/api/payments/delete",
                        paymentService.deletePayment(deletePaymentRequestDto.getPaymentId())
                ));
    }

    /*
     *  단 건 결제 조회
     */
    
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiSuccessResponse<GetPaymentsResponseDto>> getPayment(@PathVariable UUID paymentId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "/api/payments/" + paymentId,
                        paymentService.getPayment(paymentId)
                ));
    }
}
