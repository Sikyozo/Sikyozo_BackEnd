package com.spring.sikyozo.domain.order.controller;

import com.spring.sikyozo.domain.order.dto.request.*;
import com.spring.sikyozo.domain.order.dto.response.*;
import com.spring.sikyozo.domain.order.entity.OrderPaymentStatus;
import com.spring.sikyozo.domain.order.entity.OrderStatus;
import com.spring.sikyozo.domain.order.entity.OrderType;
import com.spring.sikyozo.domain.order.service.OrderService;
import com.spring.sikyozo.global.dto.ResponseDto;
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
    public ResponseEntity<ResponseDto<CreateOrderResponseDto>> createOrderByOnline(@RequestBody CreateOrderByOnlineRequestDto onlineRequestDto) {
        ResponseDto<CreateOrderResponseDto> orderByOnline = orderService.createOrderByOnline(onlineRequestDto.getUserId(), onlineRequestDto.getAddressId());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderByOnline);
    }

    /*
     * 대면 주문
     */
    @PostMapping("/offline")
    public ResponseEntity<ResponseDto<CreateOrderResponseDto>> createOrderByOffline(@RequestBody CreateOrderByOfflineRequest offlineRequestDto) {
        ResponseDto<CreateOrderResponseDto> orderByOnline = orderService.createOrderByOffline(offlineRequestDto.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderByOnline);
    }

    /*
     * 주문 취소
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ResponseDto<String>> cancelOrder(@PathVariable UUID orderId, @RequestBody CancelOrderDto cancelOrderDto) {
        ResponseDto<String> cancelOrderResponse = orderService.cancelOrder(orderId, cancelOrderDto.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(cancelOrderResponse);
    }

    /*
     * 주문 조회
     */

    /*
     * show : All, deleted, null
     */
    @GetMapping
    public ResponseEntity<ResponseDto<Page<GetOrderResponseDto>>> getOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) UUID storeId,
            @RequestParam(required = false) OrderType type,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) OrderPaymentStatus paymentStatus,
            @RequestParam(required = false) String show,
            @RequestBody GetOrdersRequestDto getOrdersRequestDto,
            Pageable pageable) {
        Long loginUserId = getOrdersRequestDto.getUserId();
        ResponseDto<Page<GetOrderResponseDto>> orderResponse = orderService.getOrders(userId, storeId, loginUserId, type, status, paymentStatus, show, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(orderResponse);
    }

    /*
     * 주문 수락
     */
    @PatchMapping("/{orderId}/accept")
    public ResponseEntity<ResponseDto<AcceptOrderResponseDto>> acceptOrder(@PathVariable UUID orderId, @RequestBody AcceptOrderRequestDto acceptOrderRequestDto) {
        ResponseDto<AcceptOrderResponseDto> acceptOrderResponse = orderService.acceptOrder(orderId, acceptOrderRequestDto.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(acceptOrderResponse);
    }

    /*
     * 주문 거절
     */

    @PatchMapping("/{orderId}/reject")
    public ResponseEntity<ResponseDto<RejectOrderResponseDto>> rejectOrder(@PathVariable UUID orderId, @RequestBody RejectOrderRequestDto rejectOrderRequestDto) {
        ResponseDto<RejectOrderResponseDto> rejectOrderResponse = orderService.rejectOrder(orderId, rejectOrderRequestDto.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(rejectOrderResponse);
    }

    /*
     * 주문 처리 완료
     */

    @PatchMapping("/{orderId}/complete")
    public ResponseEntity<ResponseDto<CompleteOrderResponseDto>> completeOrder(@PathVariable UUID orderId, @RequestBody CompleteOrderRequestDto completeOrderRequestDto) {
        ResponseDto<CompleteOrderResponseDto> completeOrderResponse = orderService.completeOrder(orderId, completeOrderRequestDto.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(completeOrderResponse);
    }

    /*
     * 주문 삭제 (소프트)
     */
    @DeleteMapping("/{orderId}/delete")
    public ResponseEntity<ResponseDto<DeleteOrderResponseDto>> deleteOrder(@PathVariable UUID orderId, @RequestBody DeleteOrderRequestDto deleteOrderRequestDto) {
        ResponseDto<DeleteOrderResponseDto> deleteOrderResponse = orderService.deleteOrder(orderId, deleteOrderRequestDto.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(deleteOrderResponse);
    }

    /*
     *  단일 주문 조회
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseDto<GetOrderResponseDto>> getOrder(@PathVariable UUID orderId, @RequestBody GetOrdersRequestDto getOrdersRequestDto) {
        ResponseDto<GetOrderResponseDto> orderResponse = orderService.getOrder(getOrdersRequestDto.getUserId(), orderId);
        return ResponseEntity.status(HttpStatus.OK).body(orderResponse);
    }
}
