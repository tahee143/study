package com.example.product_order_service.order;


import com.example.product_order_service.product.Product;

public interface OrderPort {
    Product getProductById(final Long productId);
    void save(Order order);
}
