package com.socialcommerce.wishlist;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WishlistItemDTO {
    public Long id;
    public Long productId;
    public String title;
    public BigDecimal price;
    public String primaryImageUrl;
    public boolean inStock;
    public LocalDateTime addedAt;

    public WishlistItemDTO(Long id, Long productId, String title,
                           BigDecimal price, String primaryImageUrl,
                           boolean inStock, LocalDateTime addedAt) {
        this.id = id;
        this.productId = productId;
        this.title = title;
        this.price = price;
        this.primaryImageUrl = primaryImageUrl;
        this.inStock = inStock;
        this.addedAt = addedAt;
    }
}