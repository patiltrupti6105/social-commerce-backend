package com.socialcommerce.orders.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotNull
    private Long addressId;
    private String paymentMethod;
}