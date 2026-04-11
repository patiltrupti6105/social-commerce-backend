package com.socialcommerce.orders;

public interface OrderService {
    boolean hasUserPurchasedProduct(Long userId, Long productId);
}
