package com.socialcommerce.orders.service;

import com.socialcommerce.catalog.ProductService;
import com.socialcommerce.catalog.entity.ProductVariant;
import com.socialcommerce.common.exception.InsufficientStockException;
import com.socialcommerce.common.exception.ResourceNotFoundException;
import com.socialcommerce.notifications.NotificationService;
import com.socialcommerce.notifications.document.Notification;
import com.socialcommerce.orders.dto.*;
import com.socialcommerce.orders.entity.*;
import com.socialcommerce.orders.repository.*;
import com.socialcommerce.payment.MockPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;
    private final ProductService productService;      // Person 2's service
    // NOT final — optional until Person 3 implements it
    @Autowired(required = false)
    private  NotificationService notificationService; // Person 3's service
    private final MockPaymentService mockPaymentService;
    private final OrderService orderService;

    @Transactional
    public OrderDTO checkout(Long userId, CheckoutRequest request) {
        // 1. Validate address
        Address address = addressRepository.findById(request.getAddressId())
            .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("Address does not belong to this user");
        }

        // 2. Get cart items
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 3. Validate stock and snapshot prices
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItemsList = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            // Step 3a: Get variant (calls Person 2's method)
            ProductVariant variant = productService.getProductVariantById(cartItem.getVariantId());
            if (variant == null) {
                throw new ResourceNotFoundException("Product variant not found: " + cartItem.getVariantId());
            }

            // Step 3b: Check stock — throw & rollback ALL if any fails
            if (variant.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                    "Insufficient stock for: " + variant.getProduct().getTitle() +
                    ". Available: " + variant.getStockQuantity() +
                    ", Requested: " + cartItem.getQuantity()
                );
            }

            // Step 3c: Decrement stock
            variant.setStockQuantity(variant.getStockQuantity() - cartItem.getQuantity());
            productService.saveVariant(variant); // expose this method on Person 2's ProductService

            // Step 3d: Snapshot price
            BigDecimal price = variant.getPriceOverride() != null
                ? variant.getPriceOverride()
                : variant.getProduct().getPrice();

            total = total.add(price.multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            // Step 3e: Build order item
            String variantDetails = buildVariantDetails(variant);
            orderItemsList.add(OrderItem.builder()
                .productId(variant.getProduct().getId())
                .variantId(variant.getId())
                .productTitle(variant.getProduct().getTitle())
                .variantDetails(variantDetails)
                .quantity(cartItem.getQuantity())
                .priceAtPurchase(price)
                .sellerId(variant.getProduct().getSellerId())
                .build());
        }

        // 4. Create Order
        Order order = Order.builder()
            .buyerId(userId)
            .addressId(request.getAddressId())
            .totalAmount(total)
            .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "MOCK")
            .build();
        order = orderRepository.save(order);

        // 5. Save order items with orderId
        final Long orderId = order.getId();
        orderItemsList.forEach(item -> item.setOrderId(orderId));
        orderItemRepository.saveAll(orderItemsList);

        // 6. Process mock payment
        mockPaymentService.process(order);

        // 7. Clear cart
        cartItemRepository.deleteByUserId(userId);

     // 8. Send notification (Person 3's method)
        if (notificationService != null) {
            try {
                notificationService.createNotification(
                    String.valueOf(userId),
                    Notification.NotificationType.ORDER_PLACED,
                    "system",
                    "System",
                    order.getUuid(),
                    "Your order has been placed!"
                );
            } catch (Exception e) {
                log.warn("Notification failed, skipping: {}", e.getMessage());
            }
        }

        // 9. Return OrderDTO
        return orderService.buildOrderDTO(order, orderItemsList);
    }

    private String buildVariantDetails(ProductVariant variant) {
        StringBuilder sb = new StringBuilder();
        if (variant.getSize() != null) sb.append("Size: ").append(variant.getSize());
        if (variant.getColor() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("Color: ").append(variant.getColor());
        }
        return sb.toString();
    }
}