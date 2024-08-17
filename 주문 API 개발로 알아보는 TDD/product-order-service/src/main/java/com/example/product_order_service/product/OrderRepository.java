package com.example.product_order_service.product;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class OrderRepository {

    private Map<Long, Order> persistance = new HashMap<>();
    private Long sequence = 0L;

    public void save(Order order) {
        order.assignId(++sequence);
        persistance.put(order.getId(), order);
    }
}