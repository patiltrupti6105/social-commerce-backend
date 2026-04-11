package com.socialcommerce.orders;

import com.socialcommerce.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seller/orders")
public class SellerOrderController {

    // TODO P4: inject OrderService

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getSellerOrders() {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: seller incoming orders"));
    }

    @PutMapping("/{orderId}/ship")
    public ResponseEntity<ApiResponse<?>> ship(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: mark SHIPPED"));
    }

    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<ApiResponse<?>> deliver(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: mark DELIVERED"));
    }
}
