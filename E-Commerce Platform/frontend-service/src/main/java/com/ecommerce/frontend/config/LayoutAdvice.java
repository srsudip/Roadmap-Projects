package com.ecommerce.frontend.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class LayoutAdvice {

    @ModelAttribute
    public void addLayoutAttributes(HttpServletRequest request, Model model) {
        String username = extractCookie(request, "USERNAME");
        String role = extractCookie(request, "USER_ROLE");

        model.addAttribute("loggedIn", username != null);
        model.addAttribute("username", username);
        model.addAttribute("isAdmin", "ADMIN".equals(role));
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
