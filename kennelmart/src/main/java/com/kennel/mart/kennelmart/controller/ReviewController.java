package com.kennel.mart.kennelmart.controller;

import com.kennel.mart.kennelmart.dto.ReviewRequest;
import com.kennel.mart.kennelmart.dto.ReviewResponse;
import com.kennel.mart.kennelmart.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(@Valid @RequestBody ReviewRequest request,
                                                    Authentication authentication) {
        String buyerEmail = authentication.getName();
        ReviewResponse response = reviewService.addReview(buyerEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/sellers/{sellerId}")
    public ResponseEntity<Page<ReviewResponse>> getSellerReviews(@PathVariable UUID sellerId,
                                                                 Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsForSeller(sellerId, pageable));
    }

    @GetMapping("/sellers/{sellerId}/average")
    public ResponseEntity<Double> getSellerAverageRating(@PathVariable UUID sellerId) {
        return ResponseEntity.ok(reviewService.getAverageRatingForSeller(sellerId));
    }
}