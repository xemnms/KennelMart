package com.kennel.mart.kennelmart.controller;

import com.kennel.mart.kennelmart.entity.ProductListing;
import com.kennel.mart.kennelmart.enums.ProductStatus;
import com.kennel.mart.kennelmart.repository.ProductListingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/listings")
@PreAuthorize("hasRole('ADMIN')")
public class AdminListingController {

    private final ProductListingRepository productListingRepository;

    public AdminListingController(ProductListingRepository productListingRepository) {
        this.productListingRepository = productListingRepository;
    }

    @GetMapping
    public ResponseEntity<Page<ProductListing>> getListings(
            @RequestParam(required = false) ProductStatus status,
            Pageable pageable) {
        if (status != null) {
            return ResponseEntity.ok(productListingRepository.findByStatus(status, pageable));
        }
        return ResponseEntity.ok(productListingRepository.findAll(pageable));
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
        productListingRepository.deleteById(listingId);
        return ResponseEntity.noContent().build();
    }
}