package com.socialcommerce.reviews;

import com.socialcommerce.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
public class ReviewController {

    // TODO P2: inject ReviewService

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getReviews(@PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: get reviews"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> addReview(@PathVariable Long productId,
            @RequestBody Object request) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: add review"));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<?>> deleteReview(@PathVariable Long productId, @PathVariable Long reviewId) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: delete review (ADMIN or owner)"));
    }
}
