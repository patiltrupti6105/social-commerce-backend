package com.socialcommerce.analytics.service;

import com.socialcommerce.orders.repository.OrderItemRepository;
import com.socialcommerce.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final EntityManager entityManager;

    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();

        // Total revenue (exclude cancelled)
        BigDecimal revenue = orderRepository.getTotalRevenue();
        overview.put("totalRevenue", revenue != null ? revenue : BigDecimal.ZERO);

        // Total orders
        overview.put("totalOrders", orderRepository.count());

        // Total users
        Query userQuery = entityManager.createNativeQuery("SELECT COUNT(*) FROM users");
        overview.put("totalUsers", ((Number) userQuery.getSingleResult()).longValue());

        // Total products
        Query productQuery = entityManager.createNativeQuery(
            "SELECT COUNT(*) FROM products WHERE status = 'ACTIVE'");
        overview.put("totalActiveProducts", ((Number) productQuery.getSingleResult()).longValue());

        return overview;
    }

    public List<Map<String, Object>> getTopProducts() {
        List<Object[]> results = orderItemRepository.findTopProductsByOrderCount();
        List<Map<String, Object>> topProducts = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("productId", row[0]);
            item.put("productTitle", row[1]);
            item.put("orderCount", row[2]);
            topProducts.add(item);
        }
        return topProducts;
    }

    public List<Map<String, Object>> getOrdersByDay() {
        Query query = entityManager.createNativeQuery(
            "SELECT DATE(created_at) as day, COUNT(*) as count, SUM(total_amount) as revenue " +
            "FROM orders WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
            "GROUP BY DATE(created_at) ORDER BY day DESC"
        );
        List<Object[]> results = query.getResultList();
        List<Map<String, Object>> dailyOrders = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("day", row[0].toString());
            item.put("count", row[1]);
            item.put("revenue", row[2]);
            dailyOrders.add(item);
        }
        return dailyOrders;
    }
}