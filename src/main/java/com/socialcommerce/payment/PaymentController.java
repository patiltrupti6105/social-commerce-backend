package com.socialcommerce.payment;

import com.socialcommerce.common.exception.ResourceNotFoundException;
import com.socialcommerce.common.response.ApiResponse;
import com.socialcommerce.orders.entity.Order;
import com.socialcommerce.orders.repository.OrderRepository;
import com.socialcommerce.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final MockPaymentService mockPaymentService;
    private final OrderRepository orderRepository;

    @PostMapping("/mock-pay")
    public ResponseEntity<ApiResponse<Payment>> mockPay(
            @RequestBody MockPayRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        Payment payment = mockPaymentService.process(order);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @lombok.Data
    public static class MockPayRequest {
        private Long orderId;
    }
}