package com.kennel.mart.kennelmart.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {

    private List<CartItemResponse> items;
    private BigDecimal totalPrice;
    private Integer itemCount;

    public CartResponse() {}

    public CartResponse(List<CartItemResponse> items, BigDecimal totalPrice, Integer itemCount) {
        this.items = items;
        this.totalPrice = totalPrice;
        this.itemCount = itemCount;
    }

    // Getters and setters
    public List<CartItemResponse> getItems() { return items; }
    public void setItems(List<CartItemResponse> items) { this.items = items; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public Integer getItemCount() { return itemCount; }
    public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }
}