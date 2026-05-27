package com.kennel.mart.kennelmart.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_seller", columnList = "seller_id"),
    @Index(name = "idx_buyer", columnList = "buyer_id"),
    @Index(name = "idx_order", columnList = "order_id")
})
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @Size(max = 1000)
    private String comment;

    public Review() {}

    public Review(User seller, User buyer, Order order, Integer rating, String comment) {
        this.seller = seller;
        this.buyer = buyer;
        this.order = order;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and setters
    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }

    public User getBuyer() { return buyer; }
    public void setBuyer(User buyer) { this.buyer = buyer; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}