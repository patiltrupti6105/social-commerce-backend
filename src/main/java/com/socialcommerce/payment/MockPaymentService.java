package com.socialcommerce.payment;

import com.socialcommerce.orders.entity.Order;
import com.socialcommerce.orders.repository.OrderRepository;
import com.socialcommerce.payment.entity.Payment;
import com.socialcommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MockPaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Payment process(Order order) {
        // Simulate payment success (always succeeds in mock mode)
        Payment payment = Payment.builder()
            .orderId(order.getId())
            .status(Payment.PaymentStatus.SUCCESS)
            .amount(order.getTotalAmount())
            .paymentMethod("MOCK")
            .transactionRef("MOCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .build();

        payment = paymentRepository.save(payment);

        // Update order payment status
        order.setPaymentStatus(Order.PaymentStatus.MOCK_PAID);
        orderRepository.save(order);

        return payment;
    }
}