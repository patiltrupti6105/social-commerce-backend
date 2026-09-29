package com.socialcommerce.catalog.service;

import com.socialcommerce.catalog.dto.CreateProductRequest;
import com.socialcommerce.catalog.dto.ProductDetailDTO;
import com.socialcommerce.catalog.dto.ProductSummaryDTO;
import com.socialcommerce.catalog.entity.Product;
import com.socialcommerce.catalog.entity.ProductImage;
import com.socialcommerce.catalog.entity.ProductVariant;
import com.socialcommerce.catalog.repository.ProductRepository;
import com.socialcommerce.catalog.repository.ProductVariantRepository;
import com.socialcommerce.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    @Transactional
    public ProductDetailDTO createProduct(Long sellerId, CreateProductRequest req) {
        Product p = new Product();
        p.setSellerId(sellerId);
        p.setTitle(req.getTitle());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setCategoryId(req.getCategoryId());
        p.setStatus(Product.ProductStatus.DRAFT);
        productRepository.save(p);

        if (req.getImageUrls() != null) {
            for (int i = 0; i < req.getImageUrls().size(); i++) {
                ProductImage img = new ProductImage();
                img.setProduct(p);
                img.setImageUrl(req.getImageUrls().get(i));
                img.setDisplayOrder(i);
                p.getImages().add(img);
            }
        }
        if (req.getVariants() != null) {
            for (CreateProductRequest.VariantRequest vr : req.getVariants()) {
                ProductVariant v = new ProductVariant();
                v.setProduct(p);
                v.setSize(vr.getSize());
                v.setColor(vr.getColor());
                v.setStockQuantity(vr.getStockQuantity() != null ? vr.getStockQuantity() : 0);
                v.setSku(vr.getSku());
                v.setPriceOverride(vr.getPriceOverride());
                p.getVariants().add(v);
            }
        }
        return ProductDetailDTO.from(productRepository.save(p));
    }

    public Page<ProductSummaryDTO> getActiveProducts(String keyword, Long categoryId,
            BigDecimal minPrice, BigDecimal maxPrice, String sortBy, int page, int size) {
        Sort sort = switch (sortBy != null ? sortBy : "") {
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "rating" -> Sort.by("avgRating").descending();
            default -> Sort.by("createdAt").descending();
        };
        return productRepository.searchProducts(keyword, categoryId, minPrice, maxPrice,
                PageRequest.of(page, size, sort))
            .map(ProductSummaryDTO::from);
    }

    public ProductDetailDTO getProductById(Long id) {
        return ProductDetailDTO.from(productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id)));
    }

    @Transactional
    public ProductDetailDTO updateProduct(Long id, Long sellerId, CreateProductRequest req) {
        Product p = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        if (!p.getSellerId().equals(sellerId)) throw new RuntimeException("Forbidden");
        if (p.getStatus() == Product.ProductStatus.PENDING_REVIEW)
            throw new RuntimeException("Cannot edit a product pending review");
        if (req.getTitle() != null) p.setTitle(req.getTitle());
        if (req.getDescription() != null) p.setDescription(req.getDescription());
        if (req.getPrice() != null) p.setPrice(req.getPrice());
        if (req.getCategoryId() != null) p.setCategoryId(req.getCategoryId());
        return ProductDetailDTO.from(productRepository.save(p));
    }

    @Transactional
    public void submitForReview(Long id, Long sellerId) {
        Product p = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        if (!p.getSellerId().equals(sellerId)) throw new RuntimeException("Forbidden");
        if (p.getStatus() != Product.ProductStatus.DRAFT && p.getStatus() != Product.ProductStatus.REJECTED)
            throw new RuntimeException("Can only submit DRAFT or REJECTED products");
        p.setStatus(Product.ProductStatus.PENDING_REVIEW);
        productRepository.save(p);
    }

    @Transactional
    public void archiveProduct(Long id, Long sellerId) {
        Product p = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        if (!p.getSellerId().equals(sellerId)) throw new RuntimeException("Forbidden");
        p.setStatus(Product.ProductStatus.ARCHIVED);
        productRepository.save(p);
    }

    public List<ProductSummaryDTO> getSellerProducts(Long sellerId) {
        return productRepository.findBySellerId(sellerId).stream()
            .map(ProductSummaryDTO::from).collect(Collectors.toList());
    }

    // Admin methods
    @Transactional
    public void approveProduct(Long id) {
        Product p = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        p.setStatus(Product.ProductStatus.ACTIVE);
        productRepository.save(p);
    }

    @Transactional
    public void rejectProduct(Long id, String reason) {
        Product p = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        p.setStatus(Product.ProductStatus.REJECTED);
        p.setRejectionReason(reason);
        productRepository.save(p);
    }

    public List<ProductSummaryDTO> getPendingProducts() {
        return productRepository.findByStatus(Product.ProductStatus.PENDING_REVIEW).stream()
            .map(ProductSummaryDTO::from).collect(Collectors.toList());
    }

    // Called by CartService and CheckoutService
    public ProductVariant getProductVariantById(Long variantId) {
        return productVariantRepository.findById(variantId)
            .orElseThrow(() -> new ResourceNotFoundException("Variant not found: " + variantId));
    }

    public ProductVariant saveVariant(ProductVariant variant) {
        return productVariantRepository.save(variant);
    }
}
