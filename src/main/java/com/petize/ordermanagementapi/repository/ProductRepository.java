package com.petize.ordermanagementapi.repository;

import com.petize.ordermanagementapi.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
