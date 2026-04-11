package com.socialcommerce.orders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    private Long productId;
    private Long variantId;
    private Long sellerId;
    private String productTitle;
    private String variantDetails;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
}
