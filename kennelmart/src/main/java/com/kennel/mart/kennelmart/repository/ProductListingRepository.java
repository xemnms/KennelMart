package com.kennel.mart.kennelmart.repository;

import com.kennel.mart.kennelmart.entity.ProductListing;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.ListingCategory;
import com.kennel.mart.kennelmart.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductListingRepository extends JpaRepository<ProductListing, UUID> {

    // All listings by seller (without pagination) – used for analytics
    List<ProductListing> findBySeller(User seller);

    // Seller listings (paginated) – exclude soft‑deleted
    Page<ProductListing> findBySellerAndStatusNot(User seller, ProductStatus status, Pageable pageable);

    // Non-paginated list of all seller's listings (for analytics) – exclude soft‑deleted
    List<ProductListing> findBySellerAndStatusNot(User seller, ProductStatus status);

    // Active listings (visible to buyers)
    Page<ProductListing> findByStatus(ProductStatus status, Pageable pageable);

    // Filter by category and status
    Page<ProductListing> findByCategoryAndStatus(ListingCategory category, ProductStatus status, Pageable pageable);

    // Search by title or description (case‑insensitive) and status
    Page<ProductListing> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatus(
            String titleKeyword, String descriptionKeyword, ProductStatus status, Pageable pageable);

    // Find a single listing by ID and status (e.g., check if active before adding to cart)
    Optional<ProductListing> findByIdAndStatus(UUID id, ProductStatus status);
}