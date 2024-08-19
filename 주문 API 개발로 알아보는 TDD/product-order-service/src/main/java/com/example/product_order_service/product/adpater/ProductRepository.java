package com.example.product_order_service.product.adpater;

import com.example.product_order_service.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
