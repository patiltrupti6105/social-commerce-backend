package com.socialcommerce.wishlist;

import com.socialcommerce.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishlist")
public class WishlistController {

    // TODO P3: inject WishlistService

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getWishlist() {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: get wishlist"));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<?>> addToWishlist(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: add to wishlist"));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<?>> removeFromWishlist(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: remove from wishlist"));
    }
}
