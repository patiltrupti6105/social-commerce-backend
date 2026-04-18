package com.socialcommerce.wishlist;


import com.socialcommerce.wishlist.entity.WishlistItem;
import com.socialcommerce.wishlist.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

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
}