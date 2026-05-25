package com.kennel.mart.kennelmart.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReviewResponse {
    private UUID id;
    private UUID sellerId;
    private String sellerName;
    private UUID buyerId;
    private String buyerName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    // builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private ReviewResponse response = new ReviewResponse();
        public Builder id(UUID id) { response.id = id; return this; }
        public Builder sellerId(UUID sellerId) { response.sellerId = sellerId; return this; }
        public Builder sellerName(String sellerName) { response.sellerName = sellerName; return this; }
        public Builder buyerId(UUID buyerId) { response.buyerId = buyerId; return this; }
        public Builder buyerName(String buyerName) { response.buyerName = buyerName; return this; }
        public Builder rating(Integer rating) { response.rating = rating; return this; }
        public Builder comment(String comment) { response.comment = comment; return this; }
        public Builder createdAt(LocalDateTime createdAt) { response.createdAt = createdAt; return this; }
        public ReviewResponse build() { return response; }
    }
    // getters
    public UUID getId() { return id; }
    public UUID getSellerId() { return sellerId; }
    public String getSellerName() { return sellerName; }
    public UUID getBuyerId() { return buyerId; }
    public String getBuyerName() { return buyerName; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}