package com.kennel.mart.kennelmart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CartItemRequest {

    @NotNull(message = "Listing ID is required")
    private UUID listingId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;

    public CartItemRequest() {}

    public CartItemRequest(UUID listingId, Integer quantity) {
        this.listingId = listingId;
        this.quantity = quantity;
    }

    public UUID getListingId() { return listingId; }
    public void setListingId(UUID listingId) { this.listingId = listingId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}