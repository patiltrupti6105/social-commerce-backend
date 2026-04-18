/*package com.socialcommerce.catalog;

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
*/
package com.socialcommerce.catalog;

import com.socialcommerce.catalog.entity.Product;
import com.socialcommerce.catalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.socialcommerce.catalog.entity.ProductVariant;
import com.socialcommerce.catalog.repository.ProductVariantRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductVariantRepository productVariantRepository;
    public ProductService(ProductRepository productRepository,
                          ProductVariantRepository productVariantRepository) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
    }

    public Product createProduct(Product product) {
        product.setUuid(UUID.randomUUID().toString());
        product.setStatus("DRAFT");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    public List<Product> getActiveProducts() {
        return productRepository.findByStatus("ACTIVE");
    }
    public ProductVariant getProductVariantById(Long variantId) {
        return productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
    }
}