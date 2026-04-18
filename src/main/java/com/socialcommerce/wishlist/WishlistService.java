package com.socialcommerce.wishlist;

import com.socialcommerce.wishlist.entity.WishlistItem;

import java.util.List;

public interface WishlistService {

    WishlistItem addToWishlist(Long userId, Long productId);

    void removeFromWishlist(Long userId, Long productId);

    List<WishlistItem> getWishlist(Long userId);
}