package com.example.product_order_service.product;


public interface OrderPort {
    Product getProductById(final Long productId);
    void save(Order order);
}
