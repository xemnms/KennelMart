package com.kennel.mart.kennelmart.service;

import com.kennel.mart.kennelmart.dto.ReviewRequest;
import com.kennel.mart.kennelmart.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface ReviewService {
    ReviewResponse addReview(String buyerEmail, ReviewRequest request);
    Page<ReviewResponse> getReviewsForSeller(UUID sellerId, Pageable pageable);
    Double getAverageRatingForSeller(UUID sellerId);
}