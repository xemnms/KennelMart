package com.kennel.mart.kennelmart.service.impl;

import com.kennel.mart.kennelmart.dto.CartItemRequest;
import com.kennel.mart.kennelmart.dto.CartItemResponse;
import com.kennel.mart.kennelmart.dto.CartResponse;
import com.kennel.mart.kennelmart.entity.*;
import com.kennel.mart.kennelmart.enums.ProductStatus;
import com.kennel.mart.kennelmart.repository.*;
import com.kennel.mart.kennelmart.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductListingRepository productListingRepository;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CartServiceImpl.class);

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           UserRepository userRepository,
                           ProductListingRepository productListingRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productListingRepository = productListingRepository;
    }

    @Override
    public void addItemToCart(String userEmail, CartItemRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ProductListing listing = productListingRepository.findByIdAndStatus(request.getListingId(), ProductStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Listing not available or inactive"));

        // Stock validation
        if (listing.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + listing.getStockQuantity());
        }

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart(user);
                    return cartRepository.save(newCart);
                });

        // Check if item already in cart
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getListing().getId().equals(request.getListingId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (newQuantity > listing.getStockQuantity()) {
                throw new IllegalArgumentException("Cannot add, total would exceed stock");
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem(listing, request.getQuantity());
            cart.addItem(newItem);
            cartItemRepository.save(newItem);
        }

        log.info("Added {} x {} to cart for user {}", request.getQuantity(), listing.getTitle(), userEmail);
    }

    @Override
    public void updateCartItemQuantity(String userEmail, UUID cartItemId, int quantity) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new SecurityException("You do not own this cart item");
        }

        if (quantity <= 0) {
            removeCartItem(userEmail, cartItemId);
            return;
        }

        ProductListing listing = cartItem.getListing();
        if (listing.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Max: " + listing.getStockQuantity());
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        log.info("Updated cart item {} quantity to {}", cartItemId, quantity);
    }

    @Override
    public void removeCartItem(String userEmail, UUID cartItemId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new SecurityException("You do not own this cart item");
        }

        cartItemRepository.delete(cartItem);
        log.info("Removed cart item {}", cartItemId);
    }

    @Override
    public CartResponse getCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findByUser(user).orElse(null);
        if (cart == null || cart.getItems().isEmpty()) {
            return new CartResponse(List.of(), BigDecimal.ZERO, 0);
        }

        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> {
                    BigDecimal subtotal = item.getPriceSnapshot().multiply(BigDecimal.valueOf(item.getQuantity()));
                    return CartItemResponse.builder()
                            .id(item.getId())
                            .listingId(item.getListing().getId())
                            .title(item.getTitleSnapshot())
                            .price(item.getPriceSnapshot())
                            .quantity(item.getQuantity())
                            .subtotal(subtotal)
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int itemCount = itemResponses.stream().mapToInt(CartItemResponse::getQuantity).sum();

        return new CartResponse(itemResponses, total, itemCount);
    }

    @Override
    public void clearCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findByUser(user).orElse(null);
        if (cart != null) {
            cartItemRepository.deleteAll(cart.getItems());
            cart.getItems().clear();
            log.info("Cleared cart for user {}", userEmail);
        }
    }
}