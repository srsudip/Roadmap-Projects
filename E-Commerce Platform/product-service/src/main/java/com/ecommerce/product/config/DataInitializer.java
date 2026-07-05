package com.ecommerce.product.config;

import com.ecommerce.product.model.Category;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.product.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataInitializer(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) return;

        Category electronics = categoryRepository.save(new Category("Electronics", "Electronic devices and gadgets"));
        Category clothing = categoryRepository.save(new Category("Clothing", "Apparel and accessories"));
        Category books = categoryRepository.save(new Category("Books", "Physical and digital books"));
        Category home = categoryRepository.save(new Category("Home & Garden", "Home improvement and garden supplies"));
        Category sports = categoryRepository.save(new Category("Sports", "Sports equipment and accessories"));

        productRepository.save(new Product("Wireless Bluetooth Headphones", "Premium noise-cancelling wireless headphones with 30-hour battery life", new BigDecimal("79.99"), 150, electronics.getId()));
        productRepository.save(new Product("Laptop Pro 15", "15-inch laptop with M3 chip, 16GB RAM, 512GB SSD", new BigDecimal("1299.99"), 25, electronics.getId()));
        productRepository.save(new Product("Smart Watch Series 5", "Fitness tracking smartwatch with heart rate monitor and GPS", new BigDecimal("249.99"), 75, electronics.getId()));
        productRepository.save(new Product("USB-C Hub Adapter", "7-in-1 USB-C hub with HDMI, USB 3.0, SD card reader", new BigDecimal("34.99"), 200, electronics.getId()));
        productRepository.save(new Product("Portable Charger 20000mAh", "High-capacity power bank with fast charging support", new BigDecimal("29.99"), 300, electronics.getId()));
        productRepository.save(new Product("Mechanical Keyboard RGB", "Full-size mechanical keyboard with Cherry MX switches", new BigDecimal("89.99"), 60, electronics.getId()));
        productRepository.save(new Product("4K Webcam", "Ultra HD webcam with auto-focus and built-in microphone", new BigDecimal("59.99"), 90, electronics.getId()));
        productRepository.save(new Product("Wireless Mouse", "Ergonomic wireless mouse with adjustable DPI", new BigDecimal("24.99"), 250, electronics.getId()));
        productRepository.save(new Product("Bluetooth Speaker", "Waterproof portable Bluetooth speaker with 20-hour battery", new BigDecimal("49.99"), 120, electronics.getId()));
        productRepository.save(new Product("Monitor Stand", "Adjustable aluminum monitor stand with USB hub", new BigDecimal("44.99"), 80, electronics.getId()));

        productRepository.save(new Product("Classic Denim Jacket", "Vintage-wash denim jacket with multiple pockets", new BigDecimal("59.99"), 100, clothing.getId()));
        productRepository.save(new Product("Running Shoes Pro", "Lightweight running shoes with responsive cushioning", new BigDecimal("89.99"), 150, clothing.getId()));
        productRepository.save(new Product("Cotton T-Shirt Pack", "3-pack premium cotton crew neck t-shirts", new BigDecimal("29.99"), 400, clothing.getId()));
        productRepository.save(new Product("Winter Puffer Jacket", "Waterproof insulated puffer jacket for cold weather", new BigDecimal("129.99"), 50, clothing.getId()));
        productRepository.save(new Product("Slim Fit Chinos", "Comfortable slim-fit stretch chinos in multiple colors", new BigDecimal("39.99"), 200, clothing.getId()));

        productRepository.save(new Product("Clean Code", "A Handbook of Agile Software Craftsmanship by Robert C. Martin", new BigDecimal("34.99"), 100, books.getId()));
        productRepository.save(new Product("Design Patterns", "Elements of Reusable Object-Oriented Software", new BigDecimal("44.99"), 60, books.getId()));
        productRepository.save(new Product("The Pragmatic Programmer", "Your Journey to Mastery in Software Development", new BigDecimal("39.99"), 80, books.getId()));
        productRepository.save(new Product("Atomic Habits", "An Easy & Proven Way to Build Good Habits", new BigDecimal("16.99"), 200, books.getId()));
        productRepository.save(new Product("Deep Work", "Rules for Focused Success in a Distracted World", new BigDecimal("14.99"), 150, books.getId()));

        productRepository.save(new Product("Smart LED Light Bulb", "WiFi-enabled RGB smart bulb with voice control", new BigDecimal("12.99"), 500, home.getId()));
        productRepository.save(new Product("Robot Vacuum Cleaner", "Smart robot vacuum with mapping and auto-empty", new BigDecimal("299.99"), 30, home.getId()));
        productRepository.save(new Product("Espresso Machine", "Semi-automatic espresso machine with milk frother", new BigDecimal("199.99"), 25, home.getId()));
        productRepository.save(new Product("Air Purifier HEPA", "HEPA air purifier for rooms up to 500 sq ft", new BigDecimal("149.99"), 40, home.getId()));
        productRepository.save(new Product("Garden Tool Set", "10-piece stainless steel garden tool set with carrying case", new BigDecimal("34.99"), 80, home.getId()));

        productRepository.save(new Product("Yoga Mat Premium", "Non-slip thick yoga mat with carrying strap", new BigDecimal("29.99"), 200, sports.getId()));
        productRepository.save(new Product("Adjustable Dumbbells", "5-52.5 lb adjustable dumbbell set", new BigDecimal("199.99"), 35, sports.getId()));
        productRepository.save(new Product("Resistance Bands Set", "5-band set with door anchor and handles", new BigDecimal("19.99"), 300, sports.getId()));
        productRepository.save(new Product("Jump Rope Speed", "Adjustable speed jump rope with ball bearings", new BigDecimal("14.99"), 250, sports.getId()));
        productRepository.save(new Product("Water Bottle 32oz", "Insulated stainless steel water bottle", new BigDecimal("19.99"), 400, sports.getId()));
    }
}
