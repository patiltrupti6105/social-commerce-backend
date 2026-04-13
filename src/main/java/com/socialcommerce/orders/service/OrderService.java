package com.socialcommerce.orders.service;

import com.socialcommerce.common.exception.ResourceNotFoundException;
import com.socialcommerce.orders.dto.*;
import com.socialcommerce.orders.entity.*;
import com.socialcommerce.orders.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;

    public Page<OrderDTO> getBuyerOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findByBuyerIdOrderByCreatedAtDesc(userId, pageable);
        return orders.map(order -> {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            return buildOrderDTO(order, items);
        });
    }

    public OrderDTO getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getBuyerId().equals(userId)) throw new RuntimeException("Unauthorized");
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return buildOrderDTO(order, items);
    }

    @Transactional
    public OrderDTO cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getBuyerId().equals(userId)) throw new RuntimeException("Unauthorized");
        if (order.getStatus() != Order.OrderStatus.PLACED) {
            throw new RuntimeException("Order can only be cancelled when in PLACED status");
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return buildOrderDTO(order, items);
    }

    // Seller methods
    public List<OrderItemDTO> getSellerOrderItems(Long sellerId) {
        return orderItemRepository.findBySellerId(sellerId)
            .stream().map(this::buildOrderItemDTO).collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO shipOrder(Long orderId, Long sellerId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (order.getStatus() != Order.OrderStatus.PLACED) {
            throw new RuntimeException("Order must be in PLACED status to ship");
        }
        order.setStatus(Order.OrderStatus.SHIPPED);
        orderRepository.save(order);
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return buildOrderDTO(order, items);
    }

    @Transactional
    public OrderDTO deliverOrder(Long orderId, Long sellerId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (order.getStatus() != Order.OrderStatus.SHIPPED) {
            throw new RuntimeException("Order must be in SHIPPED status to mark as delivered");
        }
        order.setStatus(Order.OrderStatus.DELIVERED);
        orderRepository.save(order);
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return buildOrderDTO(order, items);
    }

    // This method is called by Person 2's ReviewService
    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        return orderItemRepository.hasUserPurchasedProduct(userId, productId);
    }

    public OrderDTO buildOrderDTO(Order order, List<OrderItem> items) {
        AddressDTO addressDTO = null;
        if (order.getAddressId() != null) {
            addressRepository.findById(order.getAddressId()).ifPresent(addr -> {
                // will set below
            });
            addressDTO = addressRepository.findById(order.getAddressId())
                .map(addr -> AddressDTO.builder()
                    .id(addr.getId())
                    .fullName(addr.getFullName())
                    .phone(addr.getPhone())
                    .addressLine1(addr.getAddressLine1())
                    .addressLine2(addr.getAddressLine2())
                    .city(addr.getCity())
                    .state(addr.getState())
                    .pincode(addr.getPincode())
                    .isDefault(addr.getIsDefault())
                    .build())
                .orElse(null);
        }

        return OrderDTO.builder()
            .id(order.getId())
            .uuid(order.getUuid())
            .totalAmount(order.getTotalAmount())
            .status(order.getStatus())
            .paymentStatus(order.getPaymentStatus())
            .createdAt(order.getCreatedAt())
            .shippingAddress(addressDTO)
            .items(items.stream().map(this::buildOrderItemDTO).collect(Collectors.toList()))
            .build();
    }

    private OrderItemDTO buildOrderItemDTO(OrderItem item) {
        return OrderItemDTO.builder()
            .id(item.getId())
            .productId(item.getProductId())
            .variantId(item.getVariantId())
            .productTitle(item.getProductTitle())
            .variantDetails(item.getVariantDetails())
            .quantity(item.getQuantity())
            .priceAtPurchase(item.getPriceAtPurchase())
            .subtotal(item.getPriceAtPurchase().multiply(
                java.math.BigDecimal.valueOf(item.getQuantity())))
            .build();
    }
}