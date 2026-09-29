package com.socialcommerce.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.socialcommerce.payment.entity.Payment;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
}