package com.kennel.mart.kennelmart.service.impl;

import com.kennel.mart.kennelmart.dto.CheckoutRequest;
import com.kennel.mart.kennelmart.dto.OrderItemResponse;
import com.kennel.mart.kennelmart.dto.OrderResponse;
import com.kennel.mart.kennelmart.entity.*;
import com.kennel.mart.kennelmart.enums.OrderStatus;
import com.kennel.mart.kennelmart.enums.ProductStatus;
import com.kennel.mart.kennelmart.repository.*;
import com.kennel.mart.kennelmart.service.NotificationService;
import com.kennel.mart.kennelmart.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductListingRepository productListingRepository;
    private final NotificationService notificationService; // added

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderServiceImpl.class);

    public OrderServiceImpl(CartRepository cartRepository,
                            CartItemRepository cartItemRepository,
                            OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            UserRepository userRepository,
                            ProductListingRepository productListingRepository,
                            NotificationService notificationService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.productListingRepository = productListingRepository;
        this.notificationService = notificationService;
    }

    @Override
    public OrderResponse checkout(String userEmail, CheckoutRequest request) {
        User buyer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findByUser(buyer)
                .orElseThrow(() -> new IllegalStateException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Group cart items by seller
        Map<User, List<CartItem>> itemsBySeller = cart.getItems().stream()
                .collect(Collectors.groupingBy(item -> item.getListing().getSeller()));

        List<Order> createdOrders = new ArrayList<>();

        for (Map.Entry<User, List<CartItem>> entry : itemsBySeller.entrySet()) {
            User seller = entry.getKey();
            List<CartItem> sellerItems = entry.getValue();

            // Verify stock and prepare order items
            List<OrderItem> orderItems = new ArrayList<>();
            BigDecimal orderTotal = BigDecimal.ZERO;

            for (CartItem cartItem : sellerItems) {
                ProductListing listing = cartItem.getListing();
                if (listing.getStatus() != ProductStatus.ACTIVE) {
                    throw new IllegalStateException("Product " + listing.getTitle() + " is no longer available");
                }
                if (listing.getStockQuantity() < cartItem.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock for " + listing.getTitle());
                }

                // Deduct stock
                listing.setStockQuantity(listing.getStockQuantity() - cartItem.getQuantity());
                productListingRepository.save(listing);

                OrderItem orderItem = new OrderItem(
                        cartItem.getTitleSnapshot(),
                        cartItem.getQuantity(),
                        cartItem.getPriceSnapshot()
                );
                orderItem.setListing(listing);
                orderItems.add(orderItem);

                orderTotal = orderTotal.add(cartItem.getPriceSnapshot().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            }

            // Create order
            String orderNumber = generateOrderNumber();
            Order order = new Order(orderNumber, buyer, seller, orderTotal, request.getPaymentMethod());
            Order savedOrder = orderRepository.save(order);

            for (OrderItem oi : orderItems) {
                oi.setOrder(savedOrder);
                orderItemRepository.save(oi);
            }

            savedOrder.setItems(orderItems);
            createdOrders.add(savedOrder);
            log.info("Order {} created for seller {}", orderNumber, seller.getEmail());

            // Send notification to buyer
            notificationService.sendNotification(
                    buyer.getId(),
                    "Order Confirmation",
                    "Your order #" + orderNumber + " has been placed successfully. Total: PHP " + orderTotal,
                    "ORDER",
                    savedOrder.getId().toString()
            );

            // Send notification to seller
            notificationService.sendNotification(
                    seller.getId(),
                    "New Order Received",
                    "You have received a new order #" + orderNumber + " from " + buyer.getName() + ". Total: PHP " + orderTotal,
                    "ORDER",
                    savedOrder.getId().toString()
            );
        }

        // Clear cart after successful checkout
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepository.save(cart);

        // Return the first order (if multiple, you may return a list)
        return convertToResponse(createdOrders.get(0));
    }

    @Override
    public OrderResponse getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return convertToResponse(order);
    }

    @Override
    public Page<OrderResponse> getMyOrdersAsBuyer(String userEmail, Pageable pageable) {
        User buyer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return orderRepository.findByBuyer(buyer, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Page<OrderResponse> getMyOrdersAsSeller(String userEmail, Pageable pageable) {
        User seller = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return orderRepository.findBySeller(seller, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public OrderResponse updateOrderStatus(UUID orderId, String sellerEmail, OrderStatus newStatus) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getSeller().getId().equals(seller.getId())) {
            throw new SecurityException("You are not the seller of this order");
        }

        // Validate status transition
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of a delivered or cancelled order");
        }

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} status updated from {} to {} by seller {}", orderId, oldStatus, newStatus, sellerEmail);

        // Send notification to buyer about status change
        String title = "Order #" + order.getOrderNumber() + " status updated";
        String message = "Your order #" + order.getOrderNumber() + " has been updated from " + oldStatus + " to " + newStatus + ".";
        if (newStatus == OrderStatus.DELIVERED) {
            message = "Your order #" + order.getOrderNumber() + " has been delivered. Thank you for shopping!";
        }
        notificationService.sendNotification(
                order.getBuyer().getId(),
                title,
                message,
                "ORDER",
                orderId.toString()
        );

        return convertToResponse(updatedOrder);
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private OrderResponse convertToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productTitle(item.getProductTitle())
                        .quantity(item.getQuantity())
                        .price(item.getPriceSnapshot())
                        .subtotal(item.getPriceSnapshot().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .buyerId(order.getBuyer().getId())
                .buyerName(order.getBuyer().getName())
                .sellerId(order.getSeller().getId())
                .sellerName(order.getSeller().getName())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .paymentMethod(order.getPaymentMethod())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .deliveredAt(order.getDeliveredAt())
                .build();
    }
}