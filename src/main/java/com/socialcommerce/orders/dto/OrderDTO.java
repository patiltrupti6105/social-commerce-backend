package com.socialcommerce.orders.dto;

import com.socialcommerce.orders.entity.Order;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderDTO {
    private Long id;
    private String uuid;
    private BigDecimal totalAmount;
    private Order.OrderStatus status;
    private Order.PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private AddressDTO shippingAddress;
    private List<OrderItemDTO> items;
}