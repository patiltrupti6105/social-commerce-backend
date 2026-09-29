package com.socialcommerce.catalog.dto;

import com.socialcommerce.catalog.entity.Product;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductSummaryDTO {
    private Long id;
    private String uuid;
    private String title;
    private BigDecimal price;
    private String primaryImageUrl;
    private Double avgRating;
    private Integer reviewCount;
    private String status;

    public static ProductSummaryDTO from(Product p) {
        ProductSummaryDTO dto = new ProductSummaryDTO();
        dto.setId(p.getId());
        dto.setUuid(p.getUuid());
        dto.setTitle(p.getTitle());
        dto.setPrice(p.getPrice());
        dto.setAvgRating(p.getAvgRating());
        dto.setReviewCount(p.getReviewCount());
        dto.setStatus(p.getStatus().name());
        if (!p.getImages().isEmpty()) {
            dto.setPrimaryImageUrl(p.getImages().get(0).getImageUrl());
        }
        return dto;
    }
}
