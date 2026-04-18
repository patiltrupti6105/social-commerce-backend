/*package com.socialcommerce.catalog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "product")
    private List<ProductVariant> variants;
    @Column(unique = true, nullable = false)
    private String uuid;

    @Column(nullable = false)
    private Long sellerId;

    private Long categoryId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.DRAFT;
     
    private Double avgRating = 0.0;
    private Integer reviewCount = 0;
    private Integer wishlistCount = 0;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (uuid == null) uuid = java.util.UUID.randomUUID().toString();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ProductStatus {
        DRAFT, PENDING_REVIEW, ACTIVE, REJECTED, ARCHIVED
    }
 // Add to Product.java
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductImage> images = new ArrayList<>();
}
*/
package com.socialcommerce.catalog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    private Long sellerId;

    private Long categoryId;

    private String title;

    private String description;

    private Double price;

    private String status;

    private Double avgRating;

    private Integer reviewCount;

    private Integer wishlistCount;

    private String rejectionReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}