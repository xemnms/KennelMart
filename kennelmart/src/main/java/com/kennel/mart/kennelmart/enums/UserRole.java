package com.kennel.mart.kennelmart.enums;

/**
 * User roles for role-based access control.
 * 
 * - ADMIN: Full system access, can manage users and moderation
 * - USER: Standard user, can buy and sell on the marketplace
 */
public enum UserRole {
    ADMIN,  // System administrators with full access
    USER    // Regular users who can buy and sell
}
