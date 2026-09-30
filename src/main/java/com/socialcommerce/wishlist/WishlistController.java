package com.socialcommerce.wishlist;

import com.socialcommerce.common.BaseController;
import com.socialcommerce.wishlist.entity.WishlistItem;
import com.socialcommerce.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.socialcommerce.wishlist.WishlistItemDTO;
import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController extends BaseController {

    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<ApiResponse<WishlistItem>> addToWishlist(@RequestParam Long productId) {
        return ResponseEntity.ok(ApiResponse.success(
            wishlistService.addToWishlist(currentUserNumericId(), productId)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<?>> removeFromWishlist(@RequestParam Long productId) {
        wishlistService.removeFromWishlist(currentUserNumericId(), productId);
        return ResponseEntity.ok(ApiResponse.success(null, "Removed from wishlist"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WishlistItemDTO>>> getWishlist() {
        return ResponseEntity.ok(ApiResponse.success(
            wishlistService.getWishlistEnriched(currentUserNumericId())
        ));
    }
}
