package com.kennel.mart.kennelmart.service.impl;

import com.kennel.mart.kennelmart.dto.CreateListingRequest;
import com.kennel.mart.kennelmart.dto.ProductListingResponse;
import com.kennel.mart.kennelmart.dto.UpdateListingRequest;
import com.kennel.mart.kennelmart.entity.ProductImage;
import com.kennel.mart.kennelmart.entity.ProductListing;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.ListingCategory;
import com.kennel.mart.kennelmart.enums.ProductStatus;
import com.kennel.mart.kennelmart.enums.VerificationStatus;
import com.kennel.mart.kennelmart.repository.ProductImageRepository;
import com.kennel.mart.kennelmart.repository.ProductListingRepository;
import com.kennel.mart.kennelmart.repository.UserRepository;
import com.kennel.mart.kennelmart.service.ProductListingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductListingServiceImpl implements ProductListingService {

    private final ProductListingRepository productListingRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductListingServiceImpl.class);

    public ProductListingServiceImpl(ProductListingRepository productListingRepository,
                                     ProductImageRepository productImageRepository,
                                     UserRepository userRepository) {
        this.productListingRepository = productListingRepository;
        this.productImageRepository = productImageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ProductListingResponse createListing(CreateListingRequest request, String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        if (seller.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new IllegalStateException("Your account is not verified. Please complete identity verification first.");
        }

        ProductListing listing = new ProductListing(
                request.getTitle(),
                request.getDescription(),
                request.getPrice(),
                request.getStockQuantity(),
                request.getCategory(),
                seller
        );
        listing.setStatus(ProductStatus.PENDING_APPROVAL);

        ProductListing savedListing = productListingRepository.save(listing);

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                ProductImage image = new ProductImage(request.getImageUrls().get(i), i);
                savedListing.addImage(image);
            }
            productListingRepository.save(savedListing);
        }

        log.info("Listing created: {} by seller: {}", savedListing.getTitle(), seller.getEmail());
        return convertToResponse(savedListing);
    }

    @Override
    public ProductListingResponse updateListing(UUID listingId, UpdateListingRequest request, String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        ProductListing listing = productListingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        if (!listing.getSeller().getId().equals(seller.getId())) {
            throw new SecurityException("You can only edit your own listings");
        }

        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setPrice(request.getPrice());
        listing.setStockQuantity(request.getStockQuantity());
        listing.setCategory(request.getCategory());

        if (request.getImageUrls() != null) {
            productImageRepository.deleteByListing(listing);
            listing.getImages().clear();
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                ProductImage image = new ProductImage(request.getImageUrls().get(i), i);
                listing.addImage(image);
            }
        }

        ProductListing updatedListing = productListingRepository.save(listing);
        log.info("Listing updated: {}", updatedListing.getTitle());
        return convertToResponse(updatedListing);
    }

    @Override
    public void deleteListing(UUID listingId, String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        ProductListing listing = productListingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        if (!listing.getSeller().getId().equals(seller.getId())) {
            throw new SecurityException("You can only delete your own listings");
        }

        // Soft delete: set status to DELETED
        listing.setStatus(ProductStatus.DELETED);
        productListingRepository.save(listing);
        log.info("Listing soft-deleted: {}", listing.getTitle());
    }

    @Override
    public ProductListingResponse getListingById(UUID listingId) {
        ProductListing listing = productListingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        return convertToResponse(listing);
    }

    @Override
    public Page<ProductListingResponse> getMyListings(String sellerEmail, Pageable pageable) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
        // Exclude soft‑deleted listings
        Page<ProductListing> listings = productListingRepository.findBySellerAndStatusNot(seller, ProductStatus.DELETED, pageable);
        return listings.map(this::convertToResponse);
    }

    @Override
    public Page<ProductListingResponse> getActiveListings(String keyword, ListingCategory category, Pageable pageable) {
        Page<ProductListing> listings;
        if (keyword != null && !keyword.isBlank()) {
            listings = productListingRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatus(
                    keyword, keyword, ProductStatus.ACTIVE, pageable);
        } else if (category != null) {
            listings = productListingRepository.findByCategoryAndStatus(category, ProductStatus.ACTIVE, pageable);
        } else {
            listings = productListingRepository.findByStatus(ProductStatus.ACTIVE, pageable);
        }
        return listings.map(this::convertToResponse);
    }

    @Override
    public Page<ProductListingResponse> getActiveListingsBySellerId(UUID sellerId, Pageable pageable) {
        // Only return listings with status ACTIVE
        Page<ProductListing> listings = productListingRepository.findBySellerIdAndStatus(sellerId, ProductStatus.ACTIVE, pageable);
        return listings.map(this::convertToResponse);
    }

    private ProductListingResponse convertToResponse(ProductListing listing) {
        List<String> imageUrls = listing.getImages().stream()
                .sorted((i1, i2) -> i1.getDisplayOrder().compareTo(i2.getDisplayOrder()))
                .map(ProductImage::getImageUrl)
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