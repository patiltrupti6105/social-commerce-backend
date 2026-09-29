package com.socialcommerce.orders.repository;

import com.socialcommerce.orders.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    Optional<CartItem> findByUserIdAndVariantId(Long userId, Long variantId);
    void deleteByUserId(Long userId);
    boolean existsByUserIdAndVariantId(Long userId, Long variantId);

    @Query("SELECT COUNT(c) FROM CartItem c WHERE c.userId = :userId")
    int countByUserId(Long userId);
}