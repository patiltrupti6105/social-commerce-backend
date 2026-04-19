package com.socialcommerce.reviews;

import com.socialcommerce.reviews.entity.Review;
import com.socialcommerce.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Review>>> getReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getReviews(productId, page)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Review>> addReview(
            @PathVariable Long productId,
            @RequestBody Review review) {
        Long userId = currentUserId();
        return ResponseEntity.ok(ApiResponse.success(reviewService.addReview(productId, userId, review)));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<?>> deleteReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId, currentUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Review deleted"));
    }

    private Long currentUserId() {
        return Long.parseLong((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
