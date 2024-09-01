package com.spring.sikyozo.domain.payment.controller;

import com.spring.sikyozo.domain.order.dto.request.GetOrdersRequestDto;
import com.spring.sikyozo.domain.order.dto.response.GetOrderResponseDto;
import com.spring.sikyozo.domain.order.entity.Order;
import com.spring.sikyozo.domain.order.entity.OrderPaymentStatus;
import com.spring.sikyozo.domain.order.entity.OrderStatus;
import com.spring.sikyozo.domain.order.entity.OrderType;
import com.spring.sikyozo.domain.payment.dto.request.*;
import com.spring.sikyozo.domain.payment.dto.response.*;
import com.spring.sikyozo.domain.payment.entity.PaymentStatus;
import com.spring.sikyozo.domain.payment.entity.PaymentType;
import com.spring.sikyozo.domain.payment.service.PaymentService;
import com.spring.sikyozo.domain.store.entity.Store;
import com.spring.sikyozo.domain.user.entity.User;
import com.spring.sikyozo.domain.user.exception.UserNotHasPermissionException;
import com.spring.sikyozo.global.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
    public ResponseEntity<ResponseDto<CreatePaymentRseponseDto>> createPayment(@RequestBody CreatePaymentRequestDto createPaymentRequestDto) {
        ResponseDto<CreatePaymentRseponseDto> paymentResponse = paymentService.createPayment(createPaymentRequestDto.getOrderId(), createPaymentRequestDto.getUserId(), createPaymentRequestDto.getPrice());
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponse);
    }

    /*
     * 결제 진행
     */

    @PatchMapping
    public ResponseEntity<ResponseDto<ProcessPaymentResponseDto>> processPayment(@RequestBody ProcessPaymentRequestDto processPaymentRequestDto) {
        ResponseDto<ProcessPaymentResponseDto> processPaymentResponse = paymentService.processPayment(processPaymentRequestDto.getUserId(), processPaymentRequestDto.getPaymentId(), processPaymentRequestDto.getPaymentType(), processPaymentRequestDto.getPrice());
        return ResponseEntity.status(HttpStatus.CREATED).body(processPaymentResponse);
    }

    /*
     * 주문 조회
     */

    /*
     * show : All, deleted, null
     */
    @GetMapping
    public ResponseEntity<ResponseDto<Page<GetPaymentsResponseDto>>> getPayments(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) UUID storeId,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) PaymentType type,
            @RequestParam(required = false) String show,
            @RequestBody GetPaymentsRequestDto getPaymentsRequestDto,
            Pageable pageable) {
        Long loginUserId = getPaymentsRequestDto.getUserId();
        ResponseDto<Page<GetPaymentsResponseDto>> paymentsResponse = paymentService.getPayments(userId, storeId, loginUserId, type, status, show, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(paymentsResponse);
    }

    /*
     * 결제 취소
     */

    @DeleteMapping
    public ResponseEntity<ResponseDto<CancelPaymentResponseDto>> cancelPayment(@RequestBody CancelPaymentRequestDto cancelPaymentRequestDto) {
        ResponseDto<CancelPaymentResponseDto> cancelPaymentResponse = paymentService.cancelPayment(cancelPaymentRequestDto.getUserId(), cancelPaymentRequestDto.getPaymentId());
        return ResponseEntity.status(HttpStatus.OK).body(cancelPaymentResponse);
    }

    /*
     * 결제 삭제
     */

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto<DeletePaymentResponseDto>> deletePayment(@RequestBody DeletePaymentRequestDto deletePaymentRequestDto) {
        ResponseDto<DeletePaymentResponseDto> deletePaymentResponse = paymentService.deletePayment(deletePaymentRequestDto.getUserId(), deletePaymentRequestDto.getPaymentId());
        return ResponseEntity.status(HttpStatus.OK).body(deletePaymentResponse);
    }

    /*
     *  단 건 결제 조회
     */
    
    @GetMapping("/{paymentId}")
    public ResponseEntity<ResponseDto<GetPaymentsResponseDto>> getPayment(@PathVariable UUID paymentId, @RequestBody GetPaymentsRequestDto getPaymentsRequestDto) {
        ResponseDto<GetPaymentsResponseDto> paymentResponse = paymentService.getPayment(getPaymentsRequestDto.getUserId(), paymentId);
        return ResponseEntity.status(HttpStatus.OK).body(paymentResponse);
    }
}
