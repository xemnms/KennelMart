package com.kennel.mart.kennelmart.service;

import com.kennel.mart.kennelmart.dto.ReviewRequest;
import com.kennel.mart.kennelmart.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface ReviewService {

    // User submits a review (status = PENDING)
    ReviewResponse addReview(String buyerEmail, ReviewRequest request);

    // Public: get approved reviews for a seller
    Page<ReviewResponse> getReviewsForSeller(UUID sellerId, Pageable pageable);

    // Get average rating for seller (based on approved reviews)
    Double getAverageRatingForSeller(UUID sellerId);

    // Admin: get all pending reviews
    Page<ReviewResponse> getPendingReviews(Pageable pageable);

    // Admin: approve a review
    ReviewResponse approveReview(UUID reviewId);

    // Admin: reject a review
    ReviewResponse rejectReview(UUID reviewId);
}