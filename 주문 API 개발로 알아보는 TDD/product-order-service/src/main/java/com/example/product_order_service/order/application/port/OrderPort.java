package com.example.product_order_service.order.application.port;


import com.example.product_order_service.order.domain.Order;
import com.example.product_order_service.product.domain.Product;

public interface OrderPort {
    Product getProductById(final Long productId);
    void save(Order order);
}
