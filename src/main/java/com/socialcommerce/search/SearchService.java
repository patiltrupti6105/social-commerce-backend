package com.socialcommerce.search;

import org.springframework.data.domain.Pageable;

public interface SearchService {
    // TODO P2: MySQL LIKE + filters; may delegate to ProductRepository
    void search(String q, Long categoryId, Double minPrice, Double maxPrice, String sortBy, Pageable pageable);
}
