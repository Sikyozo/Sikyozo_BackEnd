package com.spring.sikyozo.domain.order.controller;

import com.spring.sikyozo.domain.order.dto.request.*;
import com.spring.sikyozo.domain.order.dto.response.*;
import com.spring.sikyozo.domain.order.entity.OrderPaymentStatus;
import com.spring.sikyozo.domain.order.entity.OrderStatus;
import com.spring.sikyozo.domain.order.entity.OrderType;
import com.spring.sikyozo.domain.order.service.OrderService;
import com.spring.sikyozo.global.dto.ResponseDto;
import com.spring.sikyozo.global.exception.dto.ApiSuccessResponse;
import com.spring.sikyozo.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /*
     * 비대면 주문
     */
    @PostMapping("/online")
    public ResponseEntity<ApiSuccessResponse<CreateOrderResponseDto>> createOrderByOnline(@RequestBody CreateOrderByOnlineRequestDto onlineRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiSuccessResponse.of(
                        HttpStatus.CREATED,
                        "/api/orders",
                        orderService.createOrderByOnline(onlineRequestDto.getAddressId())
                ));
    }

    /*
     * 대면 주문
     */
    @PostMapping("/offline")
    public ResponseEntity<ApiSuccessResponse<CreateOrderResponseDto>> createOrderByOffline() {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiSuccessResponse.of(
                        HttpStatus.CREATED,
                        "/path/offline/offline",
                        orderService.createOrderByOffline()
                ));
    }

    /*
     * 주문 취소
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiSuccessResponse<Void>> cancelOrder(@PathVariable UUID orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "/api/orders/online",
                        null
                ));
    }

    /*
     * 주문 조회
     */

    /*
     * show : All, deleted, null
     */
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<Page<GetOrderResponseDto>>> getOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) UUID storeId,
            @RequestParam(required = false) OrderType type,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) OrderPaymentStatus paymentStatus,
            @RequestParam(required = false) String show,
            Pageable pageable) {

        Page<GetOrderResponseDto> orderResponse = orderService.getOrders(userId, storeId, type, status, paymentStatus, show, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "/api/orders?userId=" + userId + "&storeId=" + storeId + "&type=" + type + "&status=" + status
                        + "&paymentStatus=" + paymentStatus + "&show=" + show,
                        orderResponse
                ));
    }

    /*
     * 주문 수락
     */
    @PatchMapping("/{orderId}/accept")
    public ResponseEntity<ApiSuccessResponse<AcceptOrderResponseDto>> acceptOrder(@PathVariable UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "/api/orders/" + orderId + "/accept",
                        orderService.acceptOrder(orderId)
                ));
    }

    /*
     * 주문 거절
     */

    @PatchMapping("/{orderId}/reject")
    public ResponseEntity<ApiSuccessResponse<RejectOrderResponseDto>> rejectOrder(@PathVariable UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "/api/orders/" + orderId + "/reject",
                        orderService.rejectOrder(orderId)
                ));
    };

    /*
     * 주문 처리 완료
     */

    @PatchMapping("/{orderId}/complete")
    public ResponseEntity<ApiSuccessResponse<CompleteOrderResponseDto>> completeOrder(@PathVariable UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "/api/orders/" + orderId + "/complete",
                        orderService.completeOrder(orderId)
                ));
    }

    /*
     * 주문 삭제 (소프트)
     */
    @DeleteMapping("/{orderId}/delete")
    public ResponseEntity<ApiSuccessResponse<DeleteOrderResponseDto>> deleteOrder(@PathVariable UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "/api/orders/" + orderId + "/delte",
                        orderService.deleteOrder(orderId)
                ));
    }

    /*
     *  단일 주문 조회
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiSuccessResponse<GetOrderResponseDto>> getOrder(@PathVariable UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "/api/orders/" + orderId,
                        orderService.getOrder(orderId)
                ));
    }
}
