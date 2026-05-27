package com.kennel.mart.kennelmart.dto;

import java.math.BigDecimal;

public class SellerAnalyticsResponse {
    private int totalOrders;
    private int totalItemsSold;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private int totalListings;
    private int activeListings;
    private int uniqueBuyers;

    // Builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private SellerAnalyticsResponse response = new SellerAnalyticsResponse();
        public Builder totalOrders(int totalOrders) { response.totalOrders = totalOrders; return this; }
        public Builder totalItemsSold(int totalItemsSold) { response.totalItemsSold = totalItemsSold; return this; }
        public Builder totalRevenue(BigDecimal totalRevenue) { response.totalRevenue = totalRevenue; return this; }
        public Builder averageOrderValue(BigDecimal averageOrderValue) { response.averageOrderValue = averageOrderValue; return this; }
        public Builder totalListings(int totalListings) { response.totalListings = totalListings; return this; }
        public Builder activeListings(int activeListings) { response.activeListings = activeListings; return this; }
        public Builder uniqueBuyers(int uniqueBuyers) { response.uniqueBuyers = uniqueBuyers; return this; }
        public SellerAnalyticsResponse build() { return response; }
    }

    // Getters
    public int getTotalOrders() { return totalOrders; }
    public int getTotalItemsSold() { return totalItemsSold; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public BigDecimal getAverageOrderValue() { return averageOrderValue; }
    public int getTotalListings() { return totalListings; }
    public int getActiveListings() { return activeListings; }
    public int getUniqueBuyers() { return uniqueBuyers; }
}