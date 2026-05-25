package com.kennel.mart.kennelmart.service;

import com.kennel.mart.kennelmart.dto.CartItemRequest;
import com.kennel.mart.kennelmart.dto.CartResponse;
import java.util.UUID;

public interface CartService {
    void addItemToCart(String userEmail, CartItemRequest request);
    void updateCartItemQuantity(String userEmail, UUID cartItemId, int quantity);
    void removeCartItem(String userEmail, UUID cartItemId);
    CartResponse getCart(String userEmail);
    void clearCart(String userEmail);
}