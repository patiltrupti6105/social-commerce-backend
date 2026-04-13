package com.socialcommerce.orders.service;

import com.socialcommerce.catalog.ProductService;
import com.socialcommerce.catalog.entity.ProductVariant;
import com.socialcommerce.common.exception.InsufficientStockException;
import com.socialcommerce.common.exception.ResourceNotFoundException;
import com.socialcommerce.orders.dto.*;
import com.socialcommerce.orders.entity.CartItem;
import com.socialcommerce.orders.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductService productService; // Person 2's service

    public CartDTO getCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        List<CartItemDTO> dtos = items.stream()
            .map(item -> buildCartItemDTO(item))
            .collect(Collectors.toList());

        BigDecimal total = dtos.stream()
            .map(CartItemDTO::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartDTO.builder()
            .items(dtos)
            .total(total)
            .itemCount(dtos.stream().mapToInt(CartItemDTO::getQuantity).sum())
            .build();
    }

    @Transactional
    public CartDTO addItem(Long userId, AddCartItemRequest request) {
        ProductVariant variant = productService.getProductVariantById(request.getVariantId());
        if (variant == null) {
            throw new ResourceNotFoundException("Variant not found");
        }
        if (variant.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock. Available: " + variant.getStockQuantity());
        }

        cartItemRepository.findByUserIdAndVariantId(userId, request.getVariantId())
            .ifPresentOrElse(
                existing -> {
                    existing.setQuantity(existing.getQuantity() + request.getQuantity());
                    cartItemRepository.save(existing);
                },
                () -> cartItemRepository.save(
                    CartItem.builder()
                        .userId(userId)
                        .variantId(request.getVariantId())
                        .quantity(request.getQuantity())
                        .build()
                )
            );

        return getCart(userId);
    }

    @Transactional
    public CartDTO updateQuantity(Long userId, Long itemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        ProductVariant variant = productService.getProductVariantById(item.getVariantId());
        if (variant.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock. Available: " + variant.getStockQuantity());
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return getCart(userId);
    }

    @Transactional
    public void removeItem(Long userId, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!item.getUserId().equals(userId)) throw new RuntimeException("Unauthorized");
        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    private CartItemDTO buildCartItemDTO(CartItem item) {
        ProductVariant variant = productService.getProductVariantById(item.getVariantId());
        BigDecimal price = variant.getPriceOverride() != null
            ? variant.getPriceOverride()
            : variant.getProduct().getPrice();
        return CartItemDTO.builder()
            .id(item.getId())
            .variantId(item.getVariantId())
            .productId(variant.getProduct().getId())
            .productTitle(variant.getProduct().getTitle())
            .primaryImageUrl(variant.getProduct().getImages().isEmpty() ? null
                : variant.getProduct().getImages().get(0).getImageUrl())
            .size(variant.getSize())
            .color(variant.getColor())
            .stockQuantity(variant.getStockQuantity())
            .price(price)
            .quantity(item.getQuantity())
            .subtotal(price.multiply(BigDecimal.valueOf(item.getQuantity())))
            .build();
    }
}