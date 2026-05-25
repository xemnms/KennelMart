package com.kennel.mart.kennelmart.service.impl;

import com.kennel.mart.kennelmart.dto.ReviewRequest;
import com.kennel.mart.kennelmart.dto.ReviewResponse;
import com.kennel.mart.kennelmart.entity.Order;
import com.kennel.mart.kennelmart.entity.Review;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.OrderStatus;
import com.kennel.mart.kennelmart.enums.ReviewStatus;
import com.kennel.mart.kennelmart.repository.OrderRepository;
import com.kennel.mart.kennelmart.repository.ReviewRepository;
import com.kennel.mart.kennelmart.repository.UserRepository;
import com.kennel.mart.kennelmart.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReviewServiceImpl.class);

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             OrderRepository orderRepository,
                             UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReviewResponse addReview(String buyerEmail, ReviewRequest request) {
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getBuyer().getId().equals(buyer.getId())) {
            throw new SecurityException("You can only review your own orders");
        }

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("You can only review orders that have been delivered");
        }

        if (reviewRepository.existsByOrderId(order.getId())) {
            throw new IllegalStateException("You have already reviewed this order");
        }

        User seller = order.getSeller();

        Review review = new Review(seller, buyer, order, request.getRating(), request.getComment());
        review.setStatus(ReviewStatus.PENDING);  // requires admin approval
        Review saved = reviewRepository.save(review);

        log.info("New review submitted (pending) for seller {} by buyer {}", seller.getEmail(), buyer.getEmail());
        return convertToResponse(saved);
    }

    @Override
    public Page<ReviewResponse> getReviewsForSeller(UUID sellerId, Pageable pageable) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
        return reviewRepository.findBySellerAndStatus(seller, ReviewStatus.APPROVED, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Double getAverageRatingForSeller(UUID sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
        Double avg = reviewRepository.calculateAverageRatingForSeller(seller);
        return avg != null ? avg : 0.0;
    }

    @Override
    public Page<ReviewResponse> getPendingReviews(Pageable pageable) {
        return reviewRepository.findByStatus(ReviewStatus.PENDING, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public ReviewResponse approveReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        review.setStatus(ReviewStatus.APPROVED);
        reviewRepository.save(review);

        // Update seller's average rating
        User seller = review.getSeller();
        Double avg = reviewRepository.calculateAverageRatingForSeller(seller);
        seller.setAverageRating(avg != null ? avg : 0.0);
        userRepository.save(seller);

        log.info("Review {} approved for seller {}", reviewId, seller.getEmail());
        return convertToResponse(review);
    }

    @Override
    public ReviewResponse rejectReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        review.setStatus(ReviewStatus.REJECTED);
        reviewRepository.save(review);
        log.info("Review {} rejected", reviewId);
        return convertToResponse(review);
    }

    private ReviewResponse convertToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .sellerId(review.getSeller().getId())
                .sellerName(review.getSeller().getName())
                .buyerId(review.getBuyer().getId())
                .buyerName(review.getBuyer().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}