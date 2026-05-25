package com.kennel.mart.kennelmart.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * ProductImage entity represents an image associated with a product listing.
 * 
 * Composition relationship: images belong to exactly one listing.
 * 
 * OOP Principles:
 * - Composition: ProductListing owns ProductImage
 * - Encapsulation: private fields with getters/setters
 */
@Entity
@Table(name = "product_images", indexes = {
    @Index(name = "idx_listing", columnList = "listing_id")
})
public class ProductImage extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private Integer displayOrder = 0;  // for sorting images

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private ProductListing listing;

    // Default constructor (JPA required)
    public ProductImage() {}

    // Convenience constructor
    public ProductImage(String imageUrl, Integer displayOrder) {
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
    }

    // Getters and setters
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public ProductListing getListing() { return listing; }
    public void setListing(ProductListing listing) { this.listing = listing; }

    @Override
    public String toString() {
        return "ProductImage{" +
                "id=" + getId() +
                ", imageUrl='" + imageUrl + '\'' +
                ", displayOrder=" + displayOrder +
                '}';
    }
}