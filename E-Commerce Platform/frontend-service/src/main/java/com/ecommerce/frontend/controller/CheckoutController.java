package com.ecommerce.frontend.controller;

import com.ecommerce.frontend.client.CartClient;
import com.ecommerce.frontend.client.OrderClient;
import com.ecommerce.frontend.client.PaymentClient;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CartClient cartClient;
    private final OrderClient orderClient;
    private final PaymentClient paymentClient;

    public CheckoutController(CartClient cartClient, OrderClient orderClient, PaymentClient paymentClient) {
        this.cartClient = cartClient;
        this.orderClient = orderClient;
        this.paymentClient = paymentClient;
    }

    @GetMapping
    public String checkoutPage(Model model, HttpServletRequest request) {
        String userId = extractCookie(request, "USER_ID");
        if (userId == null) return "redirect:/login";

        String token = extractToken(request);
        var cart = cartClient.getCart(Long.parseLong(userId), token);
        model.addAttribute("cart", cart);
        addCommonAttributes(model, request);
        return "checkout/checkout";
    }

    @PostMapping
    public String placeOrder(
            @RequestParam String shippingAddress,
            @RequestParam(defaultValue = "CREDIT_CARD") String paymentMethod,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        String userId = extractCookie(request, "USER_ID");
        if (userId == null) return "redirect:/login";

        String token = extractToken(request);
        Long userIdLong = Long.parseLong(userId);

        // Get cart
        var cart = cartClient.getCart(userIdLong, token);
        if (cart == null || cart.get("items") == null) {
            redirectAttributes.addFlashAttribute("error", "Cart is empty");
            return "redirect:/cart";
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cartItems = (List<Map<String, Object>>) cart.get("items");
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cart is empty");
            return "redirect:/cart";
        }

        // Create order items
        List<Map<String, Object>> orderItems = new ArrayList<>();
        for (var item : cartItems) {
            orderItems.add(Map.of(
                "productId", item.get("productId"),
                "productName", item.get("productName"),
                "price", item.get("price"),
                "quantity", item.get("quantity")
            ));
        }

        // Create order
        var order = orderClient.createOrder(userIdLong, orderItems, shippingAddress, token);
        if (order == null) {
            redirectAttributes.addFlashAttribute("error", "Failed to create order");
            return "redirect:/checkout";
        }

        // Process payment
        Long orderId = ((Number) order.get("id")).longValue();
        Object totalObj = order.get("totalAmount");
        String amount = totalObj != null ? totalObj.toString() : "0";

        paymentClient.processPayment(orderId, userIdLong, amount, paymentMethod, token);

        // Clear cart
        cartClient.clearCart(userIdLong, token);

        return "redirect:/checkout/confirmation/" + orderId;
    }

    @GetMapping("/confirmation/{orderId}")
    public String confirmation(@PathVariable Long orderId, Model model, HttpServletRequest request) {
        String token = extractToken(request);
        var order = orderClient.getOrder(orderId, token);
        model.addAttribute("order", order);
        addCommonAttributes(model, request);
        return "checkout/confirmation";
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
