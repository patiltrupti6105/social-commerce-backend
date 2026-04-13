package com.socialcommerce.catalog;

import com.socialcommerce.catalog.entity.ProductVariant;
import com.socialcommerce.catalog.repository.ProductVariantRepository;
import com.socialcommerce.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductVariantRepository productVariantRepository;

    // Add this method — CheckoutService calls it
    public ProductVariant getProductVariantById(Long variantId) {
        return productVariantRepository.findById(variantId)
            .orElseThrow(() -> new ResourceNotFoundException("Variant not found: " + variantId));
    }

    // Add this method — CheckoutService calls it
    public ProductVariant saveVariant(ProductVariant variant) {
        return productVariantRepository.save(variant);
    }

    // ... rest of Person 2's existing methods stay here
}
