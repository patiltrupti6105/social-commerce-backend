package com.socialcommerce.catalog.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductSummaryDTO {
    // TODO P2: id, uuid, title, price, primaryImageUrl, avgRating, reviewCount, sellerName, etc.
    private Long id;
    private String title;
    private BigDecimal price;
}
