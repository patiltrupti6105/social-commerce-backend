package com.socialcommerce.orders;

import com.socialcommerce.common.response.ApiResponse;
import com.socialcommerce.orders.dto.*;
import com.socialcommerce.orders.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final CheckoutService checkoutService;
    private final OrderService orderService;

    // ===== BUYER ENDPOINTS =====

    @PostMapping("/api/v1/orders/checkout")
    public ResponseEntity<ApiResponse<OrderDTO>> checkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CheckoutRequest request) {
        Long userId = extractUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(checkoutService.checkout(userId, request)));
    }

    @GetMapping("/api/v1/orders")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = extractUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(orderService.getBuyerOrders(userId, page, size)));
    }

    @GetMapping("/api/v1/orders/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        Long userId = extractUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(orderId, userId)));
    }

    @PutMapping("/api/v1/orders/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderDTO>> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        Long userId = extractUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(orderService.cancelOrder(orderId, userId)));
    }

    // ===== SELLER ENDPOINTS =====

    @GetMapping("/api/v1/seller/orders")
    public ResponseEntity<ApiResponse<List<OrderItemDTO>>> getSellerOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long sellerId = extractUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(orderService.getSellerOrderItems(sellerId)));
    }

    @PutMapping("/api/v1/seller/orders/{orderId}/ship")
    public ResponseEntity<ApiResponse<OrderDTO>> shipOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        Long sellerId = extractUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(orderService.shipOrder(orderId, sellerId)));
    }

    @PutMapping("/api/v1/seller/orders/{orderId}/deliver")
    public ResponseEntity<ApiResponse<OrderDTO>> deliverOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        Long sellerId = extractUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(orderService.deliverOrder(orderId, sellerId)));
    }

    private Long extractUserId(UserDetails userDetails) {
        return Long.parseLong(userDetails.getUsername());
    }
}