package com.ecommerce.frontend.controller;

import com.ecommerce.frontend.client.CartClient;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartClient cartClient;

    public CartController(CartClient cartClient) {
        this.cartClient = cartClient;
    }

    @GetMapping
    public String viewCart(Model model, HttpServletRequest request) {
        String userId = extractCookie(request, "USER_ID");
        if (userId == null) return "redirect:/login";

        String token = extractToken(request);
        var cart = cartClient.getCart(Long.parseLong(userId), token);
        model.addAttribute("cart", cart);
        addCommonAttributes(model, request);
        return "cart/cart";
    }

    @PostMapping("/update/{productId}")
    public String updateQuantity(
            @PathVariable Long productId,
            @RequestParam int quantity,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        String userId = extractCookie(request, "USER_ID");
        if (userId == null) return "redirect:/login";

        String token = extractToken(request);
        if (quantity <= 0) {
            cartClient.removeItem(Long.parseLong(userId), productId, token);
        } else {
            cartClient.updateQuantity(Long.parseLong(userId), productId, quantity, token);
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove/{productId}")
    public String removeItem(@PathVariable Long productId, HttpServletRequest request) {
        String userId = extractCookie(request, "USER_ID");
        if (userId == null) return "redirect:/login";

        String token = extractToken(request);
        cartClient.removeItem(Long.parseLong(userId), productId, token);
        return "redirect:/cart";
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
