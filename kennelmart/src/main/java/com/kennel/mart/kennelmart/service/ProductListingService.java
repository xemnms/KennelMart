package com.kennel.mart.kennelmart.service;

import com.kennel.mart.kennelmart.dto.CreateListingRequest;
import com.kennel.mart.kennelmart.dto.ProductListingResponse;
import com.kennel.mart.kennelmart.dto.UpdateListingRequest;
import com.kennel.mart.kennelmart.enums.ListingCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductListingService {
    ProductListingResponse createListing(CreateListingRequest request, String sellerEmail);
    ProductListingResponse updateListing(UUID listingId, UpdateListingRequest request, String sellerEmail);
    void deleteListing(UUID listingId, String sellerEmail);
    ProductListingResponse getListingById(UUID listingId);
    Page<ProductListingResponse> getMyListings(String sellerEmail, Pageable pageable);
    Page<ProductListingResponse> getActiveListings(String keyword, ListingCategory category, Pageable pageable);
}