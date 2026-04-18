/*package com.socialcommerce.catalog.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String size;
    private String color;

    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @Column(unique = true)
    private String sku;

    private BigDecimal priceOverride;
}*/
package com.socialcommerce.catalog.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private String size;

    private String color;

    private Integer stock;

    private Double price;
}