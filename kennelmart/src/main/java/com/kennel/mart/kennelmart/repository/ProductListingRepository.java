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

    // Find by status (active, pending_approval, etc.)
    Page<ProductListing> findByStatus(ProductStatus status, Pageable pageable);

    // Find by category and status
    Page<ProductListing> findByCategoryAndStatus(ListingCategory category, ProductStatus status, Pageable pageable);

    // Search by title or description (case-insensitive) and status
    Page<ProductListing> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatus(
            String titleKeyword, String descKeyword, ProductStatus status, Pageable pageable);

    // Find by seller and exclude a specific status (e.g., not DELETED)
    Page<ProductListing> findBySellerAndStatusNot(User seller, ProductStatus status, Pageable pageable);

    // Find by seller ID and status (used in user profile page)
    Page<ProductListing> findBySellerIdAndStatus(UUID sellerId, ProductStatus status, Pageable pageable);

    // Find by ID and status (used in CartServiceImpl)
    Optional<ProductListing> findByIdAndStatus(UUID id, ProductStatus status);

    // For analytics: get all listings of a seller (no pagination)
    List<ProductListing> findBySeller(User seller);

    // Paginated version (if needed elsewhere)
    Page<ProductListing> findBySeller(User seller, Pageable pageable);
}