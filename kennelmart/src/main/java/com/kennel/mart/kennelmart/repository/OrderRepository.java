package com.kennel.mart.kennelmart.repository;

import com.kennel.mart.kennelmart.entity.Order;
import com.kennel.mart.kennelmart.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByBuyer(User buyer, Pageable pageable);
    Page<Order> findBySeller(User seller, Pageable pageable);
}