package com.socialcommerce.catalog.dto;

import com.socialcommerce.catalog.entity.Product;
import com.socialcommerce.catalog.entity.ProductVariant;
import com.socialcommerce.catalog.entity.ProductImage;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductDetailDTO {
    private Long id;
    private String uuid;
    private Long sellerId;
    private Long categoryId;
    private String title;
    private String description;
    private BigDecimal price;
    private String status;
    private Double avgRating;
    private Integer reviewCount;
    private Integer wishlistCount;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private List<VariantDTO> variants;
    private List<String> imageUrls;

    @Data
    public static class VariantDTO {
        private Long id;
        private String size;
        private String color;
        private Integer stockQuantity;
        private String sku;
        private BigDecimal priceOverride;
    }

    public static ProductDetailDTO from(Product p) {
        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setId(p.getId());
        dto.setUuid(p.getUuid());
        dto.setSellerId(p.getSellerId());
        dto.setCategoryId(p.getCategoryId());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setStatus(p.getStatus().name());
        dto.setAvgRating(p.getAvgRating());
        dto.setReviewCount(p.getReviewCount());
        dto.setWishlistCount(p.getWishlistCount());
        dto.setRejectionReason(p.getRejectionReason());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setImageUrls(p.getImages().stream()
            .map(ProductImage::getImageUrl).collect(Collectors.toList()));
        dto.setVariants(p.getVariants().stream().map(v -> {
            VariantDTO vd = new VariantDTO();
            vd.setId(v.getId());
            vd.setSize(v.getSize());
            vd.setColor(v.getColor());
            vd.setStockQuantity(v.getStockQuantity());
            vd.setSku(v.getSku());
            vd.setPriceOverride(v.getPriceOverride());
            return vd;
        }).collect(Collectors.toList()));
        return dto;
    }
}
