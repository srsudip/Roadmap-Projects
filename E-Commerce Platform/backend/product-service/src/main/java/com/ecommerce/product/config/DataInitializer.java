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
        productRepository.save(new Product("Wireless Bluetooth Headphones", "Premium noise-cancelling wireless headphones with 30-hour battery life", new BigDecimal("74.99"), 150, electronics.getId(), "https://placehold.co/600x400/1a1a2e/ffffff?text=Headphones"));
        productRepository.save(new Product("Laptop Pro 15", "15-inch laptop with M3 chip, 16GB RAM, 512GB SSD", new BigDecimal("1199.99"), 25, electronics.getId(), "https://placehold.co/600x400/0f3460/ffffff?text=Laptop+Pro+15"));
        productRepository.save(new Product("Smart Watch Series 5", "Fitness tracking smartwatch with heart rate monitor and GPS", new BigDecimal("229.99"), 75, electronics.getId(), "https://placehold.co/600x400/e94560/ffffff?text=Smart+Watch"));
        productRepository.save(new Product("USB-C Hub Adapter", "7-in-1 USB-C hub with HDMI, USB 3.0, SD card reader", new BigDecimal("32.99"), 200, electronics.getId(), "https://placehold.co/600x400/533483/ffffff?text=USB-C+Hub"));
        productRepository.save(new Product("Portable Charger 20000mAh", "High-capacity power bank with fast charging support", new BigDecimal("27.99"), 300, electronics.getId(), "https://placehold.co/600x400/2c3e50/ffffff?text=Power+Bank"));
        productRepository.save(new Product("Mechanical Keyboard RGB", "Full-size mechanical keyboard with Cherry MX switches", new BigDecimal("84.99"), 60, electronics.getId(), "https://placehold.co/600x400/8e44ad/ffffff?text=Keyboard+RGB"));
        productRepository.save(new Product("4K Webcam", "Ultra HD webcam with auto-focus and built-in microphone", new BigDecimal("54.99"), 90, electronics.getId(), "https://placehold.co/600x400/2980b9/ffffff?text=4K+Webcam"));
        productRepository.save(new Product("Wireless Mouse", "Ergonomic wireless mouse with adjustable DPI", new BigDecimal("22.99"), 250, electronics.getId(), "https://placehold.co/600x400/27ae60/ffffff?text=Wireless+Mouse"));
        productRepository.save(new Product("Bluetooth Speaker", "Waterproof portable Bluetooth speaker with 20-hour battery", new BigDecimal("44.99"), 120, electronics.getId(), "https://placehold.co/600x400/e67e22/ffffff?text=Speaker"));
        productRepository.save(new Product("Monitor Stand", "Adjustable aluminum monitor stand with USB hub", new BigDecimal("42.99"), 80, electronics.getId(), "https://placehold.co/600x400/34495e/ffffff?text=Monitor+Stand"));

        // ── Clothing ────────────────────────────────────
        productRepository.save(new Product("Classic Denim Jacket", "Vintage-wash denim jacket with multiple pockets", new BigDecimal("54.99"), 100, clothing.getId(), "https://placehold.co/600x400/3498db/ffffff?text=Denim+Jacket"));
        productRepository.save(new Product("Running Shoes Pro", "Lightweight running shoes with responsive cushioning", new BigDecimal("84.99"), 150, clothing.getId(), "https://placehold.co/600x400/e74c3c/ffffff?text=Running+Shoes"));
        productRepository.save(new Product("Cotton T-Shirt Pack", "3-pack premium cotton crew neck t-shirts", new BigDecimal("27.99"), 400, clothing.getId(), "https://placehold.co/600x400/1abc9c/ffffff?text=T-Shirt+Pack"));
        productRepository.save(new Product("Winter Puffer Jacket", "Waterproof insulated puffer jacket for cold weather", new BigDecimal("119.99"), 50, clothing.getId(), "https://placehold.co/600x400/9b59b6/ffffff?text=Puffer+Jacket"));
        productRepository.save(new Product("Slim Fit Chinos", "Comfortable slim-fit stretch chinos in multiple colors", new BigDecimal("36.99"), 200, clothing.getId(), "https://placehold.co/600x400/f39c12/ffffff?text=Chinos"));

        // ── Books ───────────────────────────────────────
        productRepository.save(new Product("Clean Code", "A Handbook of Agile Software Craftsmanship by Robert C. Martin", new BigDecimal("32.99"), 100, books.getId(), "https://placehold.co/600x400/c0392b/ffffff?text=Clean+Code"));
        productRepository.save(new Product("Design Patterns", "Elements of Reusable Object-Oriented Software", new BigDecimal("42.99"), 60, books.getId(), "https://placehold.co/600x400/2c3e50/ffffff?text=Design+Patterns"));
        productRepository.save(new Product("The Pragmatic Programmer", "Your Journey to Mastery in Software Development", new BigDecimal("37.99"), 80, books.getId(), "https://placehold.co/600x400/16a085/ffffff?text=Pragmatic+Programmer"));
        productRepository.save(new Product("Atomic Habits", "An Easy & Proven Way to Build Good Habits", new BigDecimal("15.99"), 200, books.getId(), "https://placehold.co/600x400/d35400/ffffff?text=Atomic+Habits"));
        productRepository.save(new Product("Deep Work", "Rules for Focused Success in a Distracted World", new BigDecimal("13.99"), 150, books.getId(), "https://placehold.co/600x400/8e44ad/ffffff?text=Deep+Work"));

        // ── Home & Garden ───────────────────────────────
        productRepository.save(new Product("Smart LED Light Bulb", "WiFi-enabled RGB smart bulb with voice control", new BigDecimal("11.99"), 500, home.getId(), "https://placehold.co/600x400/f1c40f/333333?text=Smart+Bulb"));
        productRepository.save(new Product("Robot Vacuum Cleaner", "Smart robot vacuum with mapping and auto-empty", new BigDecimal("279.99"), 30, home.getId(), "https://placehold.co/600x400/2ecc71/ffffff?text=Robot+Vacuum"));
        productRepository.save(new Product("Espresso Machine", "Semi-automatic espresso machine with milk frother", new BigDecimal("184.99"), 25, home.getId(), "https://placehold.co/600x400/795548/ffffff?text=Espresso+Machine"));
        productRepository.save(new Product("Air Purifier HEPA", "HEPA air purifier for rooms up to 500 sq ft", new BigDecimal("139.99"), 40, home.getId(), "https://placehold.co/600x400/00bcd4/ffffff?text=Air+Purifier"));
        productRepository.save(new Product("Garden Tool Set", "10-piece stainless steel garden tool set with carrying case", new BigDecimal("32.99"), 80, home.getId(), "https://placehold.co/600x400/4caf50/ffffff?text=Garden+Tools"));

        // ── Sports ──────────────────────────────────────
        productRepository.save(new Product("Yoga Mat Premium", "Non-slip thick yoga mat with carrying strap", new BigDecimal("27.99"), 200, sports.getId(), "https://placehold.co/600x400/e91e63/ffffff?text=Yoga+Mat"));
        productRepository.save(new Product("Adjustable Dumbbells", "5-52.5 lb adjustable dumbbell set", new BigDecimal("184.99"), 35, sports.getId(), "https://placehold.co/600x400/607d8b/ffffff?text=Dumbbells"));
        productRepository.save(new Product("Resistance Bands Set", "5-band set with door anchor and handles", new BigDecimal("18.99"), 300, sports.getId(), "https://placehold.co/600x400/ff5722/ffffff?text=Resistance+Bands"));
        productRepository.save(new Product("Jump Rope Speed", "Adjustable speed jump rope with ball bearings", new BigDecimal("13.99"), 250, sports.getId(), "https://placehold.co/600x400/009688/ffffff?text=Jump+Rope"));
        productRepository.save(new Product("Water Bottle 32oz", "Insulated stainless steel water bottle", new BigDecimal("18.99"), 400, sports.getId(), "https://placehold.co/600x400/3f51b5/ffffff?text=Water+Bottle"));
    }
}
