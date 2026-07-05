package com.ecommerce.frontend.controller;

import com.ecommerce.frontend.client.OrderClient;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderClient orderClient;

    public OrderController(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    @GetMapping
    public String myOrders(Model model, HttpServletRequest request) {
        String userId = extractCookie(request, "USER_ID");
        if (userId == null) return "redirect:/login";

        String token = extractToken(request);
        var orders = orderClient.getUserOrders(Long.parseLong(userId), token);
        model.addAttribute("orders", orders);
        addCommonAttributes(model, request);
        return "orders/orders";
    }

    @PostMapping("/cancel/{orderId}")
    public String cancelOrder(@PathVariable Long orderId, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String token = extractToken(request);
        var result = orderClient.cancelOrder(orderId, token);
        if (result != null) {
            redirectAttributes.addFlashAttribute("success", "Order cancelled successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel order");
        }
        return "redirect:/orders";
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
