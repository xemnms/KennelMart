package com.kennel.mart.kennelmart.controller;

import com.kennel.mart.kennelmart.dto.CreateListingRequest;
import com.kennel.mart.kennelmart.dto.ProductListingResponse;
import com.kennel.mart.kennelmart.dto.UpdateListingRequest;
import com.kennel.mart.kennelmart.enums.ListingCategory;
import com.kennel.mart.kennelmart.service.ProductListingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/listings")
public class ProductListingController {

    private final ProductListingService productListingService;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductListingController.class);

    public ProductListingController(ProductListingService productListingService) {
        this.productListingService = productListingService;
    }

    // Create a new listing (seller only)
    @PostMapping
    public ResponseEntity<ProductListingResponse> createListing(@Valid @RequestBody CreateListingRequest request,
                                                                Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = authentication.getName();
        ProductListingResponse response = productListingService.createListing(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Update an existing listing (seller only)
    @PutMapping("/{id}")
    public ResponseEntity<ProductListingResponse> updateListing(@PathVariable UUID id,
                                                                @RequestBody UpdateListingRequest request,
                                                                Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = authentication.getName();
        ProductListingResponse response = productListingService.updateListing(id, request, email);
        return ResponseEntity.ok(response);
    }

    // Delete a listing (seller only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(@PathVariable UUID id,
                                              Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Delete attempt without authentication");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = authentication.getName();
        log.info("Attempting to delete listing {} by user {}", id, email);
        try {
            productListingService.deleteListing(id, email);
            log.info("Listing {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            log.warn("Security exception while deleting listing {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            log.warn("Listing not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Unexpected error while deleting listing {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get listing by ID (public)
    @GetMapping("/{id}")
    public ResponseEntity<ProductListingResponse> getListingById(@PathVariable UUID id) {
        ProductListingResponse response = productListingService.getListingById(id);
        return ResponseEntity.ok(response);
    }

    // Get current user's listings (seller dashboard)
    @GetMapping("/my-listings")
    public ResponseEntity<Page<ProductListingResponse>> getMyListings(Authentication authentication,
                                                                      @PageableDefault(size = 10) Pageable pageable) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = authentication.getName();
        Page<ProductListingResponse> responses = productListingService.getMyListings(email, pageable);
        return ResponseEntity.ok(responses);
    }

    // Public marketplace browse (active listings, with optional search and category filter)
    @GetMapping
    public ResponseEntity<Page<ProductListingResponse>> browseListings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ListingCategory category,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<ProductListingResponse> responses = productListingService.getActiveListings(keyword, category, pageable);
        return ResponseEntity.ok(responses);
    }
}