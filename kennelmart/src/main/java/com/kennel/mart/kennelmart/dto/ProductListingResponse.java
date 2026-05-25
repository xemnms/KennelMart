package com.kennel.mart.kennelmart.dto;

import com.kennel.mart.kennelmart.enums.ListingCategory;
import com.kennel.mart.kennelmart.enums.ProductStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProductListingResponse {
    private UUID id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private ProductStatus status;
    private ListingCategory category;
    private UUID sellerId;
    private String sellerName;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Builder pattern
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private ProductListingResponse response = new ProductListingResponse();
        public Builder id(UUID id) { response.id = id; return this; }
        public Builder title(String title) { response.title = title; return this; }
        public Builder description(String description) { response.description = description; return this; }
        public Builder price(BigDecimal price) { response.price = price; return this; }
        public Builder stockQuantity(Integer stockQuantity) { response.stockQuantity = stockQuantity; return this; }
        public Builder status(ProductStatus status) { response.status = status; return this; }
        public Builder category(ListingCategory category) { response.category = category; return this; }
        public Builder sellerId(UUID sellerId) { response.sellerId = sellerId; return this; }
        public Builder sellerName(String sellerName) { response.sellerName = sellerName; return this; }
        public Builder imageUrls(List<String> imageUrls) { response.imageUrls = imageUrls; return this; }
        public Builder createdAt(LocalDateTime createdAt) { response.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { response.updatedAt = updatedAt; return this; }
        public ProductListingResponse build() { return response; }
    }

    // Getters (no setters for immutability)
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public Integer getStockQuantity() { return stockQuantity; }
    public ProductStatus getStatus() { return status; }
    public ListingCategory getCategory() { return category; }
    public UUID getSellerId() { return sellerId; }
    public String getSellerName() { return sellerName; }
    public List<String> getImageUrls() { return imageUrls; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}