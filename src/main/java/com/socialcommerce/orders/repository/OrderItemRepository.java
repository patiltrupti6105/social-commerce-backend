package com.socialcommerce.orders.repository;

import com.socialcommerce.orders.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.sellerId = :sellerId ORDER BY oi.id DESC")
    List<OrderItem> findBySellerId(Long sellerId);

    @Query("SELECT CASE WHEN COUNT(oi) > 0 THEN TRUE ELSE FALSE END FROM OrderItem oi " +
           "JOIN Order o ON oi.orderId = o.id " +
           "WHERE o.buyerId = :userId AND oi.productId = :productId AND o.status = 'DELIVERED'")
    boolean hasUserPurchasedProduct(Long userId, Long productId);

    @Query(value = "SELECT oi.product_id, oi.product_title, COUNT(*) as order_count " +
                   "FROM order_items oi GROUP BY oi.product_id, oi.product_title " +
                   "ORDER BY order_count DESC LIMIT 10",
           nativeQuery = true)
    List<Object[]> findTopProductsByOrderCount();
}