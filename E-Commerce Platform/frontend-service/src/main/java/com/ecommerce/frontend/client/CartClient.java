package com.ecommerce.frontend.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class CartClient {

    private final RestTemplate restTemplate;
    private final String gatewayUrl;

    public CartClient(RestTemplate restTemplate, @Value("${api.gateway.url}") String gatewayUrl) {
        this.restTemplate = restTemplate;
        this.gatewayUrl = gatewayUrl;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getCart(Long userId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (token != null) headers.setBearerAuth(token);
            var response = restTemplate.exchange(
                gatewayUrl + "/api/cart/" + userId,
                HttpMethod.GET, new HttpEntity<>(headers), Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> addItem(Long userId, Long productId, String productName, String price, int quantity, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (token != null) headers.setBearerAuth(token);
            var request = Map.of(
                "productId", productId,
                "productName", productName,
                "price", price,
                "quantity", quantity
            );
            var response = restTemplate.exchange(
                gatewayUrl + "/api/cart/" + userId + "/items",
                HttpMethod.POST, new HttpEntity<>(request, headers), Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> updateQuantity(Long userId, Long productId, int quantity, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (token != null) headers.setBearerAuth(token);
            var response = restTemplate.exchange(
                gatewayUrl + "/api/cart/" + userId + "/items/" + productId + "?quantity=" + quantity,
                HttpMethod.PUT, new HttpEntity<>(headers), Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> removeItem(Long userId, Long productId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (token != null) headers.setBearerAuth(token);
            var response = restTemplate.exchange(
                gatewayUrl + "/api/cart/" + userId + "/items/" + productId,
                HttpMethod.DELETE, new HttpEntity<>(headers), Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public void clearCart(Long userId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (token != null) headers.setBearerAuth(token);
            restTemplate.exchange(
                gatewayUrl + "/api/cart/" + userId,
                HttpMethod.DELETE, new HttpEntity<>(headers), Void.class
            );
        } catch (Exception e) {
            // ignore
        }
    }
}
