package com.socialcommerce.orders;

import com.socialcommerce.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    // TODO P4: inject OrderService, CheckoutService

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<?>> checkout(@RequestBody Object request) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: checkout"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getOrders() {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: get order history (buyer)"));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<?>> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: get order detail"));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<?>> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: cancel order"));
    }
}
