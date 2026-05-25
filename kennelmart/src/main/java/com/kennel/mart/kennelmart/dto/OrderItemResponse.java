package com.kennel.mart.kennelmart.dto;

import java.math.BigDecimal;

public class OrderItemResponse {

    private String productTitle;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private OrderItemResponse response = new OrderItemResponse();
        public Builder productTitle(String productTitle) { response.productTitle = productTitle; return this; }
        public Builder quantity(Integer quantity) { response.quantity = quantity; return this; }
        public Builder price(BigDecimal price) { response.price = price; return this; }
        public Builder subtotal(BigDecimal subtotal) { response.subtotal = subtotal; return this; }
        public OrderItemResponse build() { return response; }
    }

    // Getters
    public String getProductTitle() { return productTitle; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getSubtotal() { return subtotal; }
}