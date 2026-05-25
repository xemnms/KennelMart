package com.kennel.mart.kennelmart.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CartItemResponse {

    private UUID id;
    private UUID listingId;
    private String title;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;

    // Builder pattern
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private CartItemResponse response = new CartItemResponse();
        public Builder id(UUID id) { response.id = id; return this; }
        public Builder listingId(UUID listingId) { response.listingId = listingId; return this; }
        public Builder title(String title) { response.title = title; return this; }
        public Builder price(BigDecimal price) { response.price = price; return this; }
        public Builder quantity(Integer quantity) { response.quantity = quantity; return this; }
        public Builder subtotal(BigDecimal subtotal) { response.subtotal = subtotal; return this; }
        public CartItemResponse build() { return response; }
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getListingId() { return listingId; }
    public String getTitle() { return title; }
    public BigDecimal getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getSubtotal() { return subtotal; }
}