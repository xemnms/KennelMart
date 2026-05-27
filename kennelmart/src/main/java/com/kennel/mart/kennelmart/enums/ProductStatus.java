package com.kennel.mart.kennelmart.enums;

/**
 * Product listing status for marketplace listings.
 * 
 * Tracks the status of product listings in the marketplace.
 */
public enum ProductStatus {
    ACTIVE,
    INACTIVE,
    SOLD_OUT,
    PENDING_APPROVAL,
    DELETED   // soft‑deleted listings (still in DB but hidden)
}