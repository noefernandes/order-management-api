package com.petize.ordermanagementapi.service;

import com.petize.ordermanagementapi.exception.ProductNotFound;
import com.petize.ordermanagementapi.model.product.Product;
import com.petize.ordermanagementapi.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    public Product createProduct(Product product) {
        return this.productRepository.save(product);
    }

    public void deleteProductById(Long id) {
        this.productRepository.findById(id).orElseThrow(
                () -> new ProductNotFound("Product not found with id: " + id)
        );
        this.productRepository.deleteById(id);
    }

    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new ProductNotFound("Product not found with id: " + id)
        );
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
