package com.kennel.mart.kennelmart.repository;

import com.kennel.mart.kennelmart.entity.Review;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    // Public: only approved reviews for a seller
    Page<Review> findBySellerAndStatus(User seller, ReviewStatus status, Pageable pageable);

    // Admin: find by status (e.g., PENDING)
    Page<Review> findByStatus(ReviewStatus status, Pageable pageable);

    // Check if a review already exists for an order
    boolean existsByOrderId(UUID orderId);

    // Calculate average rating for a seller (all approved reviews)
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.seller = :seller AND r.status = 'APPROVED'")
    Double calculateAverageRatingForSeller(User seller);
}