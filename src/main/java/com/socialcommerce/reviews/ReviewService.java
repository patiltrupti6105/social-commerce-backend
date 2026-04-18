/*package com.socialcommerce.reviews;

public interface ReviewService {
    // TODO P2: implement; call OrderService.hasUserPurchasedProduct before create
}
*/
package com.socialcommerce.reviews;

import com.socialcommerce.reviews.entity.Review;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review addReview(Review review) {
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    public List<Review> getReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
}