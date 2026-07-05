package com.ecommerce.frontend.controller;

import com.ecommerce.frontend.client.CartClient;
import com.ecommerce.frontend.client.ProductClient;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductClient productClient;
    private final CartClient cartClient;

    public ProductController(ProductClient productClient, CartClient cartClient) {
        this.productClient = productClient;
        this.cartClient = cartClient;
    }

    @GetMapping
    public String catalog(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            Model model,
            HttpServletRequest request) {

        var products = productClient.getAllProducts();
        var categories = productClient.getAllCategories();

        if (search != null && !search.isBlank()) {
            products = productClient.searchProducts(search);
            model.addAttribute("search", search);
        } else if (categoryId != null) {
            products = productClient.getProductsByCategory(categoryId);
            model.addAttribute("selectedCategory", categoryId);
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        addCommonAttributes(model, request);
        return "products/catalog";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpServletRequest request) {
        var product = productClient.getProduct(id);
        if (product == null) {
            return "redirect:/products";
        }
        model.addAttribute("product", product);
        addCommonAttributes(model, request);
        return "products/detail";
    }

    @PostMapping("/add-to-cart/{id}")
    public String addToCart(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int quantity,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        String token = extractToken(request);
        String userId = extractCookie(request, "USER_ID");
        if (userId == null) return "redirect:/login";

        var product = productClient.getProduct(id);
        if (product != null) {
            cartClient.addItem(
                Long.parseLong(userId), id,
                (String) product.get("name"),
                String.valueOf(product.get("price")),
                quantity, token
            );
            redirectAttributes.addFlashAttribute("success", "Added to cart!");
        }

        return "redirect:/products/" + id;
    }

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        String username = extractCookie(request, "USERNAME");
        String role = extractCookie(request, "USER_ROLE");
        String userId = extractCookie(request, "USER_ID");
        model.addAttribute("loggedIn", username != null);
        model.addAttribute("username", username);
        model.addAttribute("isAdmin", "ADMIN".equals(role));
        model.addAttribute("userId", userId);
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
