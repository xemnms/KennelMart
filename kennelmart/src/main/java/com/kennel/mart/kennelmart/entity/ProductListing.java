package com.kennel.mart.kennelmart.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.kennel.mart.kennelmart.enums.ListingCategory;
import com.kennel.mart.kennelmart.enums.ProductStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * ProductListing entity represents a product or service offered by a seller.
 * 
 * Relationships:
 * - Many-to-One with User (seller)
 * - One-to-Many with ProductImage (composition)
 * 
 * OOP Principles:
 * - Encapsulation: private fields with getters/setters
 * - Composition: listing owns its images
 */
@Entity
@Table(name = "product_listings", indexes = {
    @Index(name = "idx_seller", columnList = "seller_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_category", columnList = "category")
})
public class ProductListing extends BaseEntity {

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    // Composition: images belong to this listing, cascade all operations
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    // Default constructor (required by JPA)
    public ProductListing() {}

    // Convenience constructor with essential fields
    public ProductListing(String title, String description, BigDecimal price, 
                          Integer stockQuantity, ListingCategory category, User seller) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.seller = seller;
        this.status = ProductStatus.ACTIVE;
    }

    // Helper methods to manage bidirectional relationship with ProductImage
    public void addImage(ProductImage image) {
        images.add(image);
        image.setListing(this);
    }

    public void removeImage(ProductImage image) {
        images.remove(image);
        image.setListing(null);
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public ListingCategory getCategory() {
        return category;
    }

    public void setCategory(ListingCategory category) {
        this.category = category;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "ProductListing{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", category=" + category +
                '}';
    }
}