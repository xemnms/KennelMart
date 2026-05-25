package com.kennel.mart.kennelmart.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id")
    private ProductListing listing; // optional (can be null if listing deleted later)

    private String productTitle;
    private Integer quantity;
    private BigDecimal priceSnapshot;

    public OrderItem() {}

    public OrderItem(String productTitle, Integer quantity, BigDecimal priceSnapshot) {
        this.productTitle = productTitle;
        this.quantity = quantity;
        this.priceSnapshot = priceSnapshot;
    }

    // Getters and setters
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public ProductListing getListing() { return listing; }
    public void setListing(ProductListing listing) { this.listing = listing; }

    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPriceSnapshot() { return priceSnapshot; }
    public void setPriceSnapshot(BigDecimal priceSnapshot) { this.priceSnapshot = priceSnapshot; }
}