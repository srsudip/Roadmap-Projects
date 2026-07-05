package com.ecommerce.frontend.controller;

import com.ecommerce.frontend.client.AuthClient;
import com.ecommerce.frontend.client.OrderClient;
import com.ecommerce.frontend.client.ProductClient;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductClient productClient;
    private final OrderClient orderClient;
    private final AuthClient authClient;

    public AdminController(ProductClient productClient, OrderClient orderClient, AuthClient authClient) {
        this.productClient = productClient;
        this.orderClient = orderClient;
        this.authClient = authClient;
    }

    @GetMapping
    public String dashboard(Model model, HttpServletRequest request) {
        String token = extractToken(request);
        var orders = orderClient.getAllOrders(token);
        var products = productClient.getAllProducts();
        var users = authClient.getAllUsers(token);

        long totalRevenue = orders.stream()
            .filter(o -> "DELIVERED".equals(o.get("status")) || "SHIPPED".equals(o.get("status")))
            .mapToLong(o -> {
                Object amt = o.get("totalAmount");
                return amt != null ? ((Number) amt).longValue() : 0;
            })
            .sum();

        model.addAttribute("totalOrders", orders.size());
        model.addAttribute("totalProducts", products.size());
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("recentOrders", orders.stream().limit(10).toList());
        model.addAttribute("products", products);
        addCommonAttributes(model, request);
        return "admin/dashboard";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model, HttpServletRequest request) {
        var categories = productClient.getAllCategories();
        model.addAttribute("categories", categories);
        addCommonAttributes(model, request);
        return "admin/product_form";
    }

    @PostMapping("/products")
    public String createProduct(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam double price,
            @RequestParam(defaultValue = "0") int stock,
            @RequestParam(required = false) Long categoryId,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        String token = extractToken(request);
        var product = new java.util.HashMap<String, Object>();
        product.put("name", name);
        product.put("description", description);
        product.put("price", price);
        product.put("stock", stock);
        product.put("categoryId", categoryId);
        product.put("active", true);

        var result = productClient.createProduct(product, token);
        if (result != null) {
            redirectAttributes.addFlashAttribute("success", "Product created successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to create product");
        }
        return "redirect:/admin";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String token = extractToken(request);
        productClient.deleteProduct(id, token);
        redirectAttributes.addFlashAttribute("success", "Product deleted");
        return "redirect:/admin";
    }

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        String username = extractCookie(request, "USERNAME");
        String role = extractCookie(request, "USER_ROLE");
        model.addAttribute("loggedIn", username != null);
        model.addAttribute("username", username);
        model.addAttribute("isAdmin", "ADMIN".equals(role));
    }

    private String extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("JWT_TOKEN".equals(c.getName())) return c.getValue();
            }
        }
        return null;
    }

    private String extractCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (name.equals(c.getName())) return c.getValue();
            }
        }
        return null;
    }
}
