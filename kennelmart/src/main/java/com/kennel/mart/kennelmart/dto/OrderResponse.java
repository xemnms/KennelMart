package com.kennel.mart.kennelmart.dto;

import com.kennel.mart.kennelmart.enums.OrderStatus;
import com.kennel.mart.kennelmart.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderResponse {

    private UUID id;
    private String orderNumber;
    private UUID buyerId;
    private String buyerName;
    private UUID sellerId;
    private String sellerName;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private PaymentMethod paymentMethod;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private OrderResponse response = new OrderResponse();
        public Builder id(UUID id) { response.id = id; return this; }
        public Builder orderNumber(String orderNumber) { response.orderNumber = orderNumber; return this; }
        public Builder buyerId(UUID buyerId) { response.buyerId = buyerId; return this; }
        public Builder buyerName(String buyerName) { response.buyerName = buyerName; return this; }
        public Builder sellerId(UUID sellerId) { response.sellerId = sellerId; return this; }
        public Builder sellerName(String sellerName) { response.sellerName = sellerName; return this; }
        public Builder status(OrderStatus status) { response.status = status; return this; }
        public Builder totalPrice(BigDecimal totalPrice) { response.totalPrice = totalPrice; return this; }
        public Builder paymentMethod(PaymentMethod paymentMethod) { response.paymentMethod = paymentMethod; return this; }
        public Builder items(List<OrderItemResponse> items) { response.items = items; return this; }
        public Builder createdAt(LocalDateTime createdAt) { response.createdAt = createdAt; return this; }
        public Builder deliveredAt(LocalDateTime deliveredAt) { response.deliveredAt = deliveredAt; return this; }
        public OrderResponse build() { return response; }
    }

    // Getters
    public UUID getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public UUID getBuyerId() { return buyerId; }
    public String getBuyerName() { return buyerName; }
    public UUID getSellerId() { return sellerId; }
    public String getSellerName() { return sellerName; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public List<OrderItemResponse> getItems() { return items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
}