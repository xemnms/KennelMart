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
import java.util.UUID;

@Repository
public interface ProductListingRepository extends JpaRepository<ProductListing, UUID> {

    // Find all listings by seller (for seller dashboard)
    Page<ProductListing> findBySeller(User seller, Pageable pageable);
    
    // Find active listings (visible to buyers) – with pagination and sorting
    Page<ProductListing> findByStatus(ProductStatus status, Pageable pageable);

    // Filter by category and status
    Page<ProductListing> findByCategoryAndStatus(ListingCategory category, ProductStatus status, Pageable pageable);

    // Search by title or description (case-insensitive)
    Page<ProductListing> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatus(
            String titleKeyword, String descriptionKeyword, ProductStatus status, Pageable pageable);
}