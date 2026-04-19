package com.socialcommerce.catalog.repository;

import com.socialcommerce.catalog.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByStatus(Product.ProductStatus status, Pageable pageable);
    List<Product> findByStatus(Product.ProductStatus status);
    List<Product> findBySellerId(Long sellerId);
    Optional<Product> findByUuid(String uuid);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND " +
           "(:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:categoryId IS NULL OR p.categoryId = :categoryId) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> searchProducts(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    @EntityGraph(attributePaths = {"images", "variants"})
    @Query("SELECT p FROM Product p WHERE p.id IN :ids")
    List<Product> findAllByIdWithDetails(@Param("ids") List<Long> ids);
}
