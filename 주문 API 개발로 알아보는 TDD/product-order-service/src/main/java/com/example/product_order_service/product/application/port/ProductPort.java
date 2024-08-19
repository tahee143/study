package com.example.product_order_service.product.application.port;

import com.example.product_order_service.product.domain.Product;

public interface ProductPort {
    void save(final Product product);

    Product getProduct(long productId);
}