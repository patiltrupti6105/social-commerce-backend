package com.socialcommerce.search;

import com.socialcommerce.catalog.dto.ProductSummaryDTO;
import com.socialcommerce.catalog.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ProductService productService;

    public Page<ProductSummaryDTO> search(String keyword, Long categoryId,
            BigDecimal minPrice, BigDecimal maxPrice, String sortBy, int page, int size) {
        return productService.getActiveProducts(keyword, categoryId, minPrice, maxPrice, sortBy, page, size);
    }
}
