package com.socialcommerce.payment;

import com.socialcommerce.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    // TODO P4: inject MockPaymentService

    @PostMapping("/mock-pay")
    public ResponseEntity<ApiResponse<?>> mockPay(@RequestBody Object request) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: process mock payment"));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<?>> getPaymentStatus(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: get payment status"));
    }
}
