package com.ecommerce.frontend.controller;

import com.ecommerce.frontend.client.AuthClient;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthClient authClient;

    public AuthController(AuthClient authClient) {
        this.authClient = authClient;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/products";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        var result = authClient.login(username, password);
        if (result != null && result.containsKey("token")) {
            Cookie cookie = new Cookie("JWT_TOKEN", (String) result.get("token"));
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(86400);
            response.addCookie(cookie);

            // Store userId and role in session cookie
            Cookie userCookie = new Cookie("USER_ID", String.valueOf(result.get("userId")));
            userCookie.setPath("/");
            userCookie.setMaxAge(86400);
            response.addCookie(userCookie);

            Cookie roleCookie = new Cookie("USER_ROLE", (String) result.get("role"));
            roleCookie.setPath("/");
            roleCookie.setMaxAge(86400);
            response.addCookie(roleCookie);

            Cookie usernameCookie = new Cookie("USERNAME", (String) result.get("username"));
            usernameCookie.setPath("/");
            usernameCookie.setMaxAge(86400);
            response.addCookie(usernameCookie);

            return "redirect:/products";
        }

        redirectAttributes.addFlashAttribute("error", "Invalid username or password");
        return "redirect:/login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String fullName,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        var result = authClient.register(username, email, password, fullName);
        if (result != null && result.containsKey("token")) {
            Cookie cookie = new Cookie("JWT_TOKEN", (String) result.get("token"));
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(86400);
            response.addCookie(cookie);

            Cookie userCookie = new Cookie("USER_ID", String.valueOf(result.get("userId")));
            userCookie.setPath("/");
            userCookie.setMaxAge(86400);
            response.addCookie(userCookie);

            Cookie roleCookie = new Cookie("USER_ROLE", (String) result.get("role"));
            roleCookie.setPath("/");
            roleCookie.setMaxAge(86400);
            response.addCookie(roleCookie);

            Cookie usernameCookie = new Cookie("USERNAME", (String) result.get("username"));
            usernameCookie.setPath("/");
            usernameCookie.setMaxAge(86400);
            response.addCookie(usernameCookie);

            return "redirect:/products";
        }

        redirectAttributes.addFlashAttribute("error", "Registration failed. Username or email may already exist.");
        return "redirect:/signup";
    }
}
