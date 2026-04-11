package com.socialcommerce.catalog;

import com.socialcommerce.catalog.entity.Product;
import com.socialcommerce.catalog.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductVariant getProductVariantById(Long variantId);
    void approveProduct(Long id);
    void rejectProduct(Long id, String reason);
    Page<Product> getPendingProducts(Pageable pageable);
}
