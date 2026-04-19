package com.socialcommerce.catalog.controller;

import com.socialcommerce.catalog.dto.CreateProductRequest;
import com.socialcommerce.catalog.dto.ProductDetailDTO;
import com.socialcommerce.catalog.dto.ProductSummaryDTO;
import com.socialcommerce.catalog.service.ProductService;
import com.socialcommerce.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductSummaryDTO>>> getProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            productService.getActiveProducts(q, categoryId, minPrice, maxPrice, sortBy, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
    }
    // GET /api/v1/products/seller/my/:id  — seller fetches own product for editing
    @GetMapping("/seller/my/{id}")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> getMyProduct(@PathVariable Long id) {
        Long sellerId = currentUserId();
        ProductDetailDTO dto = productService.getProductById(id);
        if (!dto.getSellerId().equals(sellerId)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Forbidden"));
        }
        return ResponseEntity.ok(ApiResponse.success(dto));
    }
    @PostMapping
    public ResponseEntity<ApiResponse<ProductDetailDTO>> createProduct(@RequestBody CreateProductRequest req) {
        Long sellerId = currentUserId();
        return ResponseEntity.ok(ApiResponse.success(productService.createProduct(sellerId, req), "Product created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> updateProduct(
            @PathVariable Long id, @RequestBody CreateProductRequest req) {
        return ResponseEntity.ok(ApiResponse.success(productService.updateProduct(id, currentUserId(), req)));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<?>> submitProduct(@PathVariable Long id) {
        productService.submitForReview(id, currentUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Submitted for review"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> archiveProduct(@PathVariable Long id) {
        productService.archiveProduct(id, currentUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Product archived"));
    }

    @GetMapping("/seller/my")
    public ResponseEntity<ApiResponse<List<ProductSummaryDTO>>> getMyProducts() {
        return ResponseEntity.ok(ApiResponse.success(productService.getSellerProducts(currentUserId())));
    }

    private Long currentUserId() {
        return Long.parseLong((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
