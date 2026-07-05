package com.ecommerce.frontend.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class OrderClient {

    private final RestTemplate restTemplate;
    private final String gatewayUrl;

    public OrderClient(RestTemplate restTemplate, @Value("${api.gateway.url}") String gatewayUrl) {
        this.restTemplate = restTemplate;
        this.gatewayUrl = gatewayUrl;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> createOrder(Long userId, List<Map<String, Object>> items, String shippingAddress, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (token != null) headers.setBearerAuth(token);
            var request = Map.of(
                "userId", userId,
                "items", items,
                "shippingAddress", shippingAddress
            );
            var response = restTemplate.exchange(
                gatewayUrl + "/api/orders",
                HttpMethod.POST, new HttpEntity<>(request, headers), Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getOrder(Long orderId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (token != null) headers.setBearerAuth(token);
            var response = restTemplate.exchange(
                gatewayUrl + "/api/orders/" + orderId,
                HttpMethod.GET, new HttpEntity<>(headers), Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getUserOrders(Long userId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (token != null) headers.setBearerAuth(token);
            var response = restTemplate.exchange(
                gatewayUrl + "/api/orders/user/" + userId,
                HttpMethod.GET, new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Map<String, Object>> getAllOrders(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (token != null) headers.setBearerAuth(token);
            var response = restTemplate.exchange(
                gatewayUrl + "/api/orders",
                HttpMethod.GET, new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            return List.of();
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> cancelOrder(Long orderId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (token != null) headers.setBearerAuth(token);
            var response = restTemplate.exchange(
                gatewayUrl + "/api/orders/" + orderId + "/cancel",
                HttpMethod.PUT, new HttpEntity<>(headers), Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
