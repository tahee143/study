package com.example.product_order_service.order.adapter;

import com.example.product_order_service.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}