package com.kennel.mart.kennelmart.controller;

import com.kennel.mart.kennelmart.dto.ProductListingResponse;
import com.kennel.mart.kennelmart.entity.ProductListing;
import com.kennel.mart.kennelmart.enums.ProductStatus;
import com.kennel.mart.kennelmart.repository.ProductListingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/listings")
public class AdminListingController {

    private final ProductListingRepository productListingRepository;

    public AdminListingController(ProductListingRepository productListingRepository) {
        this.productListingRepository = productListingRepository;
    }

    @GetMapping
    public ResponseEntity<Page<ProductListingResponse>> getListings(
            @RequestParam(required = false) ProductStatus status,
            Pageable pageable) {
        Page<ProductListing> listings;
        if (status != null) {
            listings = productListingRepository.findByStatus(status, pageable);
        } else {
            listings = productListingRepository.findAll(pageable);
        }
        Page<ProductListingResponse> response = listings.map(this::convertToResponse);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{listingId}/approve")
    public ResponseEntity<Void> approveListing(@PathVariable UUID listingId) {
        ProductListing listing = productListingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        listing.setStatus(ProductStatus.ACTIVE);
        productListingRepository.save(listing);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{listingId}/reject")
    public ResponseEntity<Void> rejectListing(@PathVariable UUID listingId) {
        ProductListing listing = productListingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        listing.setStatus(ProductStatus.INACTIVE);
        productListingRepository.save(listing);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{listingId}")
    public ResponseEntity<Void> deleteListing(@PathVariable UUID listingId) {
        ProductListing listing = productListingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        // Set status to DELETED if not already
        if (listing.getStatus() != ProductStatus.DELETED) {
            listing.setStatus(ProductStatus.DELETED);
            productListingRepository.save(listing);
        }
        return ResponseEntity.noContent().build();
    }

    private ProductListingResponse convertToResponse(ProductListing listing) {
        List<String> imageUrls = listing.getImages().stream()
                .sorted((i1, i2) -> i1.getDisplayOrder().compareTo(i2.getDisplayOrder()))
                .map(img -> img.getImageUrl())
                .collect(Collectors.toList());

        return ProductListingResponse.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .price(listing.getPrice())
                .stockQuantity(listing.getStockQuantity())
                .status(listing.getStatus())
                .category(listing.getCategory())
                .sellerId(listing.getSeller().getId())
                .sellerName(listing.getSeller().getName())
                .imageUrls(imageUrls)
                .createdAt(listing.getCreatedAt())
                .updatedAt(listing.getUpdatedAt())
                .build();
    }
}