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

        // ── Electronics ─────────────────────────────────
        productRepository.save(new Product("Wireless Bluetooth Headphones", "Premium noise-cancelling wireless headphones with 30-hour battery life", new BigDecimal("74.99"), 150, electronics.getId(), "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Laptop Pro 15", "15-inch laptop with M3 chip, 16GB RAM, 512GB SSD", new BigDecimal("1199.99"), 25, electronics.getId(), "https://images.unsplash.com/photo-1607182194705-5734dd06fed7?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Smart Watch Series 5", "Fitness tracking smartwatch with heart rate monitor and GPS", new BigDecimal("229.99"), 75, electronics.getId(), "https://images.unsplash.com/photo-1546868871-af0de0ae72be?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("USB-C Hub Adapter", "7-in-1 USB-C hub with HDMI, USB 3.0, SD card reader", new BigDecimal("32.99"), 200, electronics.getId(), "https://images.unsplash.com/photo-1625842268584-8f3296236761?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Portable Charger 20000mAh", "High-capacity power bank with fast charging support", new BigDecimal("27.99"), 300, electronics.getId(), "https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Mechanical Keyboard RGB", "Full-size mechanical keyboard with Cherry MX switches", new BigDecimal("84.99"), 60, electronics.getId(), "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("4K Webcam", "Ultra HD webcam with auto-focus and built-in microphone", new BigDecimal("54.99"), 90, electronics.getId(), "https://images.unsplash.com/photo-1587826080692-f439cd0b70da?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Wireless Mouse", "Ergonomic wireless mouse with adjustable DPI", new BigDecimal("22.99"), 250, electronics.getId(), "https://images.unsplash.com/photo-1615663245857-ac4c2c33463d?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Bluetooth Speaker", "Waterproof portable Bluetooth speaker with 20-hour battery", new BigDecimal("44.99"), 120, electronics.getId(), "https://images.unsplash.com/photo-1608043152269-423dbba4e7e2?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Monitor Stand", "Adjustable aluminum monitor stand with USB hub", new BigDecimal("42.99"), 80, electronics.getId(), "https://images.unsplash.com/photo-1611532736597-de2d4265fba3?w=600&h=400&fit=crop&auto=format"));

        // ── Clothing ────────────────────────────────────
        productRepository.save(new Product("Classic Denim Jacket", "Vintage-wash denim jacket with multiple pockets", new BigDecimal("54.99"), 100, clothing.getId(), "https://images.unsplash.com/photo-1721029194707-07589dacf836?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Running Shoes Pro", "Lightweight running shoes with responsive cushioning", new BigDecimal("84.99"), 150, clothing.getId(), "https://images.unsplash.com/photo-1637437757614-6491c8e915b5?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Cotton T-Shirt Pack", "3-pack premium cotton crew neck t-shirts", new BigDecimal("27.99"), 400, clothing.getId(), "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Winter Puffer Jacket", "Waterproof insulated puffer jacket for cold weather", new BigDecimal("119.99"), 50, clothing.getId(), "https://images.unsplash.com/photo-1544923246-77307dd270b2?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Slim Fit Chinos", "Comfortable slim-fit stretch chinos in multiple colors", new BigDecimal("36.99"), 200, clothing.getId(), "https://images.unsplash.com/photo-1473966968600-fa801b869a1a?w=600&h=400&fit=crop&auto=format"));

        // ── Books ───────────────────────────────────────
        productRepository.save(new Product("Clean Code", "A Handbook of Agile Software Craftsmanship by Robert C. Martin", new BigDecimal("32.99"), 100, books.getId(), "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Design Patterns", "Elements of Reusable Object-Oriented Software", new BigDecimal("42.99"), 60, books.getId(), "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("The Pragmatic Programmer", "Your Journey to Mastery in Software Development", new BigDecimal("37.99"), 80, books.getId(), "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Atomic Habits", "An Easy & Proven Way to Build Good Habits", new BigDecimal("15.99"), 200, books.getId(), "https://images.unsplash.com/photo-1510915361894-db8b60106cb1?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Deep Work", "Rules for Focused Success in a Distracted World", new BigDecimal("13.99"), 150, books.getId(), "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=600&h=400&fit=crop&auto=format"));

        // ── Home & Garden ───────────────────────────────
        productRepository.save(new Product("Smart LED Light Bulb", "WiFi-enabled RGB smart bulb with voice control", new BigDecimal("11.99"), 500, home.getId(), "https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Robot Vacuum Cleaner", "Smart robot vacuum with mapping and auto-empty", new BigDecimal("279.99"), 30, home.getId(), "https://images.unsplash.com/photo-1558317374-067fb5f30001?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Espresso Machine", "Semi-automatic espresso machine with milk frother", new BigDecimal("184.99"), 25, home.getId(), "https://images.unsplash.com/photo-1754847551888-f1d8d043fd7f?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Air Purifier HEPA", "HEPA air purifier for rooms up to 500 sq ft", new BigDecimal("139.99"), 40, home.getId(), "https://images.unsplash.com/photo-1585771724684-18fc09f24906?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Garden Tool Set", "10-piece stainless steel garden tool set with carrying case", new BigDecimal("32.99"), 80, home.getId(), "https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=600&h=400&fit=crop&auto=format"));

        // ── Sports ──────────────────────────────────────
        productRepository.save(new Product("Yoga Mat Premium", "Non-slip thick yoga mat with carrying strap", new BigDecimal("27.99"), 200, sports.getId(), "https://images.unsplash.com/photo-1592432678016-e910b452f9a2?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Adjustable Dumbbells", "5-52.5 lb adjustable dumbbell set", new BigDecimal("184.99"), 35, sports.getId(), "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Resistance Bands Set", "5-band set with door anchor and handles", new BigDecimal("18.99"), 300, sports.getId(), "https://images.unsplash.com/photo-1598289431512-b97b0917affc?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Jump Rope Speed", "Adjustable speed jump rope with ball bearings", new BigDecimal("13.99"), 250, sports.getId(), "https://images.unsplash.com/photo-1434596922112-19c563067271?w=600&h=400&fit=crop&auto=format"));
        productRepository.save(new Product("Water Bottle 32oz", "Insulated stainless steel water bottle", new BigDecimal("18.99"), 400, sports.getId(), "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=600&h=400&fit=crop&auto=format"));
    }
}
