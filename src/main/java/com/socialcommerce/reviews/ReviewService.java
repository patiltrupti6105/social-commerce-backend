package com.socialcommerce.reviews;

import com.socialcommerce.reviews.entity.Review;
import com.socialcommerce.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional
    public Review addReview(Long productId, Long buyerId, Review review) {
        review.setProductId(productId);
        review.setBuyerId(buyerId);
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    public Page<Review> getReviews(Long productId, int page) {
        return reviewRepository.findByProductId(productId, PageRequest.of(page, 10));
    }

    public List<Review> getReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));
        if (!review.getBuyerId().equals(userId))
            throw new RuntimeException("Forbidden");
        reviewRepository.delete(review);
    }
}
