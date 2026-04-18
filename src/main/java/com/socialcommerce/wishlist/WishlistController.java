package com.socialcommerce.wishlist;


import com.socialcommerce.wishlist.entity.WishlistItem;
import com.socialcommerce.wishlist.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    // Add to wishlist
    @PostMapping("/add")
    public WishlistItem addToWishlist(@RequestParam Long userId,
                                      @RequestParam Long productId) {
        return wishlistService.addToWishlist(userId, productId);
    }

    // Remove from wishlist
    @DeleteMapping("/remove")
    public String removeFromWishlist(@RequestParam Long userId,
                                    @RequestParam Long productId) {
        wishlistService.removeFromWishlist(userId, productId);
        return "Removed from wishlist";
    }

    // Get wishlist
    @GetMapping("/{userId}")
    public List<WishlistItem> getWishlist(@PathVariable Long userId) {
        return wishlistService.getWishlist(userId);
    }
}