package com.kennel.mart.kennelmart.enums;

/**
 * Order status progression for order management.
 * 
 * Defines the lifecycle states of an order from creation to delivery.
 */
public enum OrderStatus {
    ORDER_RECEIVED,
    ACCEPTED,
    PREPARING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
