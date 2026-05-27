package com.kennel.mart.kennelmart.service.impl;

import com.kennel.mart.kennelmart.dto.ReviewRequest;
import com.kennel.mart.kennelmart.dto.ReviewResponse;
import com.kennel.mart.kennelmart.entity.Order;
import com.kennel.mart.kennelmart.entity.Review;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.OrderStatus;
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
        Review saved = reviewRepository.save(review);

        // Update seller's average rating
        Double avg = reviewRepository.calculateAverageRatingForSeller(seller);
        if (avg != null) {
            seller.setAverageRating(avg);
            userRepository.save(seller);
        }

        log.info("New review added for seller {} by buyer {}", seller.getEmail(), buyer.getEmail());
        return convertToResponse(saved);
    }

    @Override
    public Page<ReviewResponse> getReviewsForSeller(UUID sellerId, Pageable pageable) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
        return reviewRepository.findBySeller(seller, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Double getAverageRatingForSeller(UUID sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
        Double avg = reviewRepository.calculateAverageRatingForSeller(seller);
        return avg != null ? avg : 0.0;
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