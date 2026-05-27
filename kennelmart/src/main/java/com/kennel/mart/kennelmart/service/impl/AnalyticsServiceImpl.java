package com.kennel.mart.kennelmart.service.impl;

import com.kennel.mart.kennelmart.dto.SellerAnalyticsResponse;
import com.kennel.mart.kennelmart.entity.Order;
import com.kennel.mart.kennelmart.entity.ProductListing;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.OrderStatus;
import com.kennel.mart.kennelmart.enums.ProductStatus;
import com.kennel.mart.kennelmart.repository.OrderRepository;
import com.kennel.mart.kennelmart.repository.ProductListingRepository;
import com.kennel.mart.kennelmart.repository.UserRepository;
import com.kennel.mart.kennelmart.service.AnalyticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final OrderRepository orderRepository;
    private final ProductListingRepository productListingRepository;
    private final UserRepository userRepository;

    public AnalyticsServiceImpl(OrderRepository orderRepository,
                                ProductListingRepository productListingRepository,
                                UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productListingRepository = productListingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public SellerAnalyticsResponse getSellerAnalytics(UUID sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        // All orders where seller is the seller (completed orders only for revenue)
        List<Order> sellerOrders = orderRepository.findBySeller(seller);
        List<Order> completedOrders = sellerOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .collect(Collectors.toList());

        // Total orders (all statuses) and total revenue (only delivered)
        int totalOrders = sellerOrders.size();
        BigDecimal totalRevenue = completedOrders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Average order value (over delivered orders) – using RoundingMode.HALF_UP
        BigDecimal averageOrderValue = completedOrders.isEmpty() ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(completedOrders.size()), 2, RoundingMode.HALF_UP);

        // Total items sold (sum of quantities from order items of delivered orders)
        int totalItemsSold = completedOrders.stream()
                .flatMap(order -> order.getItems().stream())
                .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0)
                .sum();

        // Unique buyers (from delivered orders)
        long uniqueBuyers = completedOrders.stream()
                .map(order -> order.getBuyer().getId())
                .distinct()
                .count();

        // Listings stats – now uses the new `findBySeller` method
        List<ProductListing> sellerListings = productListingRepository.findBySeller(seller);
        int totalListings = sellerListings.size();
        long activeListings = sellerListings.stream()
                .filter(listing -> listing.getStatus() == ProductStatus.ACTIVE)
                .count();

        return SellerAnalyticsResponse.builder()
                .totalOrders(totalOrders)
                .totalItemsSold(totalItemsSold)
                .totalRevenue(totalRevenue)
                .averageOrderValue(averageOrderValue)
                .totalListings(totalListings)
                .activeListings((int) activeListings)
                .uniqueBuyers((int) uniqueBuyers)
                .build();
    }
}