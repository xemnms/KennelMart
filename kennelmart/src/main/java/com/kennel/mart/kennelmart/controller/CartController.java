package com.kennel.mart.kennelmart.controller;

import com.kennel.mart.kennelmart.dto.CartItemRequest;
import com.kennel.mart.kennelmart.dto.CartResponse;
import com.kennel.mart.kennelmart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public ResponseEntity<Void> addItemToCart(@Valid @RequestBody CartItemRequest request,
                                              Authentication authentication) {
        String userEmail = authentication.getName();
        cartService.addItemToCart(userEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<Void> updateCartItemQuantity(@PathVariable UUID cartItemId,
                                                       @RequestParam int quantity,
                                                       Authentication authentication) {
        String userEmail = authentication.getName();
        cartService.updateCartItemQuantity(userEmail, cartItemId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable UUID cartItemId,
                                               Authentication authentication) {
        String userEmail = authentication.getName();
        cartService.removeCartItem(userEmail, cartItemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        String userEmail = authentication.getName();
        CartResponse cart = cartService.getCart(userEmail);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        String userEmail = authentication.getName();
        cartService.clearCart(userEmail);
        return ResponseEntity.noContent().build();
    }
}