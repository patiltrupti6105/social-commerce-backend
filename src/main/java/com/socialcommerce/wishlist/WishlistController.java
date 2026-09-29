package com.socialcommerce.wishlist;

import com.socialcommerce.wishlist.entity.WishlistItem;
import com.socialcommerce.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.socialcommerce.wishlist.WishlistItemDTO;
import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<ApiResponse<WishlistItem>> addToWishlist(@RequestParam Long productId) {
        return ResponseEntity.ok(ApiResponse.success(
            wishlistService.addToWishlist(currentUserId(), productId)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<?>> removeFromWishlist(@RequestParam Long productId) {
        wishlistService.removeFromWishlist(currentUserId(), productId);
        return ResponseEntity.ok(ApiResponse.success(null, "Removed from wishlist"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WishlistItemDTO>>> getWishlist() {
        return ResponseEntity.ok(ApiResponse.success(
            wishlistService.getWishlistEnriched(currentUserId())
        ));
    }

    private Long currentUserId() {
        return Long.parseLong((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
