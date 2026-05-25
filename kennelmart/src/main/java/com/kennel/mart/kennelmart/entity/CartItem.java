package com.kennel.mart.kennelmart.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
public class CartItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private ProductListing listing;

    private Integer quantity;

    @Column(precision = 10, scale = 2)
    private BigDecimal priceSnapshot; // price at addition time

    private String titleSnapshot;     // title snapshot

    public CartItem() {}

    public CartItem(ProductListing listing, Integer quantity) {
        this.listing = listing;
        this.quantity = quantity;
        this.priceSnapshot = listing.getPrice();
        this.titleSnapshot = listing.getTitle();
    }

    // Getters and setters
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public ProductListing getListing() { return listing; }
    public void setListing(ProductListing listing) { this.listing = listing; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPriceSnapshot() { return priceSnapshot; }
    public void setPriceSnapshot(BigDecimal priceSnapshot) { this.priceSnapshot = priceSnapshot; }

    public String getTitleSnapshot() { return titleSnapshot; }
    public void setTitleSnapshot(String titleSnapshot) { this.titleSnapshot = titleSnapshot; }
}