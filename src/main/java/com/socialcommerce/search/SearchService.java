/*package com.socialcommerce.search;

import org.springframework.data.domain.Pageable;

public interface SearchService {
    // TODO P2: MySQL LIKE + filters; may delegate to ProductRepository
    void search(String q, Long categoryId, Double minPrice, Double maxPrice, String sortBy, Pageable pageable);
}
*/
package com.socialcommerce.search;

import com.socialcommerce.catalog.entity.Product;
import com.socialcommerce.catalog.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final ProductRepository productRepository;

    public SearchService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> search(String keyword) {
        return productRepository.findByTitleContainingIgnoreCase(keyword);
    }
}