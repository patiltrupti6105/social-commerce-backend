package com.socialcommerce.catalog.repository;

import com.socialcommerce.catalog.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByStatus(Product.ProductStatus status, Pageable pageable);
    List<Product> findBySellerId(Long sellerId);

    // TODO P2: add search query with filters
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND " +
           "(:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchProducts(String keyword, Pageable pageable);
}
