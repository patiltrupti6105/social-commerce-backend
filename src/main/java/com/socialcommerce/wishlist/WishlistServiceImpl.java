package com.socialcommerce.wishlist;

import com.socialcommerce.catalog.entity.Product;
import com.socialcommerce.catalog.repository.ProductRepository;
import com.socialcommerce.wishlist.entity.WishlistItem;
import com.socialcommerce.wishlist.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public WishlistItem addToWishlist(Long userId, Long productId) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("Already in wishlist");
        }
        WishlistItem item = new WishlistItem();
        item.setUserId(userId);
        item.setProductId(productId);
        item.setAddedAt(LocalDateTime.now());
        return wishlistRepository.save(item);
    }

    @Override
    public void removeFromWishlist(Long userId, Long productId) {
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    public List<WishlistItem> getWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    @Override
    public List<WishlistItemDTO> getWishlistEnriched(Long userId) {
        List<WishlistItem> items = wishlistRepository.findByUserId(userId);

        List<Long> productIds = items.stream()
            .map(WishlistItem::getProductId)
            .collect(Collectors.toList());

        Map<Long, Product> productMap = productRepository.findAllByIdWithDetails(productIds)
            .stream()
            .collect(Collectors.toMap(Product::getId, p -> p));

        return items.stream().map(item -> {
            Product p = productMap.get(item.getProductId());
            if (p == null) return null;

            String imageUrl = (p.getImages() != null && !p.getImages().isEmpty())
                ? p.getImages().get(0).getImageUrl()
                : null;

            boolean inStock = p.getVariants() != null && p.getVariants().stream()
                .anyMatch(v -> v.getStockQuantity() != null && v.getStockQuantity() > 0);

            return new WishlistItemDTO(
                item.getId(),
                p.getId(),
                p.getTitle(),
                p.getPrice(),
                imageUrl,
                inStock,
                item.getAddedAt()
            );
        }).filter(dto -> dto != null).collect(Collectors.toList());
    }
}