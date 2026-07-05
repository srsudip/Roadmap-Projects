package com.ecommerce.frontend.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class AuthClient {

    private final RestTemplate restTemplate;
    private final String gatewayUrl;

    public AuthClient(RestTemplate restTemplate, @Value("${api.gateway.url}") String gatewayUrl) {
        this.restTemplate = restTemplate;
        this.gatewayUrl = gatewayUrl;
    }

    public Map<String, Object> login(String username, String password) {
        try {
            var request = Map.of("username", username, "password", password);
            var response = restTemplate.postForEntity(gatewayUrl + "/api/users/login", request, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (Exception e) {
            // login failed
        }
        return null;
    }

    public Map<String, Object> register(String username, String email, String password, String fullName) {
        try {
            var request = Map.of(
                "username", username,
                "email", email,
                "password", password,
                "fullName", fullName
            );
            var response = restTemplate.postForEntity(gatewayUrl + "/api/users/register", request, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (Exception e) {
            // register failed
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getUserFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
                Map<String, Object> user = new java.util.HashMap<>();
                user.put("token", token);

                int subStart = payload.indexOf("\"sub\":\"") + 7;
                int subEnd = payload.indexOf("\"", subStart);
                if (subEnd > subStart) user.put("username", payload.substring(subStart, subEnd));

                int userIdStart = payload.indexOf("\"userId\":") + 9;
                int userIdEnd = payload.indexOf(",", userIdStart);
                if (userIdEnd == -1) userIdEnd = payload.indexOf("}", userIdStart);
                if (userIdEnd > userIdStart) {
                    long userId = Long.parseLong(payload.substring(userIdStart, userIdEnd).trim());
                    user.put("userId", userId);
                }

                int roleStart = payload.indexOf("\"role\":\"") + 8;
                int roleEnd = payload.indexOf("\"", roleStart);
                if (roleEnd > roleStart) user.put("role", payload.substring(roleStart, roleEnd));

                return user;
            }
        } catch (Exception e) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getUserById(Long userId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (token != null) headers.setBearerAuth(token);
            var entity = new HttpEntity<>(headers);
            var response = restTemplate.exchange(
                gatewayUrl + "/api/users/" + userId,
                HttpMethod.GET, entity, Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<Map<String, Object>> getAllUsers(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (token != null) headers.setBearerAuth(token);
            var entity = new HttpEntity<>(headers);
            var response = restTemplate.exchange(
                gatewayUrl + "/api/users",
                HttpMethod.GET, entity,
                new org.springframework.core.ParameterizedTypeReference<java.util.List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            return java.util.List.of();
        }
    }
}
