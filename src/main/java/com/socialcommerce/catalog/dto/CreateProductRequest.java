package com.socialcommerce.catalog.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateProductRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private List<VariantRequest> variants;
    private List<String> imageUrls;

    @Data
    public static class VariantRequest {
        private String size;
        private String color;
        private Integer stockQuantity;
        private String sku;
        private BigDecimal priceOverride;
    }
}
