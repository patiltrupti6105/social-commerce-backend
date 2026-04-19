package com.socialcommerce.orders.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CartItemDTO {
    private Long id;
    private Long variantId;
    private Long productId;
    private String productTitle;
    private String primaryImageUrl;
    private String size;
    private String color;
    private Integer stockQuantity;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}