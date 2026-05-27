package com.kennel.mart.kennelmart.repository;

import com.kennel.mart.kennelmart.entity.Review;
import com.kennel.mart.kennelmart.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findBySeller(User seller, Pageable pageable);

    boolean existsByOrderId(UUID orderId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.seller = :seller")
    Double calculateAverageRatingForSeller(User seller);
}