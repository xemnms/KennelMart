package com.kennel.mart.kennelmart.repository;

import com.kennel.mart.kennelmart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
}