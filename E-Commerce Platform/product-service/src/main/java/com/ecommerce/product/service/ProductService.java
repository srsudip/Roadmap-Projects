package com.ecommerce.product.service;

import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findByActiveTrue();
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId);
    }

    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updated) {
        Product product = getProductById(id);
        if (updated.getName() != null) product.setName(updated.getName());
        if (updated.getDescription() != null) product.setDescription(updated.getDescription());
        if (updated.getPrice() != null) product.setPrice(updated.getPrice());
        if (updated.getImageUrl() != null) product.setImageUrl(updated.getImageUrl());
        if (updated.getStock() != null) product.setStock(updated.getStock());
        if (updated.getCategoryId() != null) product.setCategoryId(updated.getCategoryId());
        if (updated.getActive() != null) product.setActive(updated.getActive());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        product.setActive(false);
        productRepository.save(product);
    }

    public boolean updateStock(Long productId, int quantity) {
        Product product = getProductById(productId);
        if (product.getStock() >= quantity) {
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
            return true;
        }
        return false;
    }
}
