package com.socialcommerce.orders;

import com.socialcommerce.common.response.ApiResponse;
import com.socialcommerce.orders.dto.*;
import com.socialcommerce.orders.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final CheckoutService checkoutService;
    private final OrderService orderService;

    @PostMapping("/api/v1/orders/checkout")
    public ResponseEntity<ApiResponse<OrderDTO>> checkout(@Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(ApiResponse.success(checkoutService.checkout(currentUserId(), request)));
    }

    @GetMapping("/api/v1/orders")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getBuyerOrders(currentUserId(), page, size)));
    }

    @GetMapping("/api/v1/orders/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(orderId, currentUserId())));
    }

    @PutMapping("/api/v1/orders/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderDTO>> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.cancelOrder(orderId, currentUserId())));
    }

    @GetMapping("/api/v1/seller/orders")
    public ResponseEntity<ApiResponse<List<OrderItemDTO>>> getSellerOrders(
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getSellerOrderItems(currentUserId())));
    }

    @PutMapping("/api/v1/seller/orders/{orderId}/ship")
    public ResponseEntity<ApiResponse<OrderDTO>> shipOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.shipOrder(orderId, currentUserId())));
    }

    @PutMapping("/api/v1/seller/orders/{orderId}/deliver")
    public ResponseEntity<ApiResponse<OrderDTO>> deliverOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.deliverOrder(orderId, currentUserId())));
    }

    private Long currentUserId() {
        return Long.parseLong((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
