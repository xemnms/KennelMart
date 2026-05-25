package com.kennel.mart.kennelmart.controller;

import com.kennel.mart.kennelmart.dto.CheckoutRequest;
import com.kennel.mart.kennelmart.dto.OrderResponse;
import com.kennel.mart.kennelmart.enums.OrderStatus;
import com.kennel.mart.kennelmart.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody CheckoutRequest request,
                                                  Authentication authentication) {
        String userEmail = authentication.getName();
        OrderResponse response = orderService.checkout(userEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(Authentication authentication,
                                                           @PageableDefault(size = 10) Pageable pageable) {
        String userEmail = authentication.getName();
        Page<OrderResponse> orders = orderService.getMyOrdersAsBuyer(userEmail, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/my-sales")
    public ResponseEntity<Page<OrderResponse>> getMySales(Authentication authentication,
                                                          @PageableDefault(size = 10) Pageable pageable) {
        String userEmail = authentication.getName();
        Page<OrderResponse> orders = orderService.getMyOrdersAsSeller(userEmail, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID orderId) {
        OrderResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable UUID orderId,
                                                           @RequestParam OrderStatus status,
                                                           Authentication authentication) {
        String userEmail = authentication.getName();
        OrderResponse updated = orderService.updateOrderStatus(orderId, userEmail, status);
        return ResponseEntity.ok(updated);
    }
}