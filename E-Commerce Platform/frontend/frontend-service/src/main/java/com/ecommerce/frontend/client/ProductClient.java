package com.ecommerce.frontend.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class ProductClient {

    private final RestTemplate restTemplate;
    private final String gatewayUrl;

    public ProductClient(RestTemplate restTemplate, @Value("${api.gateway.url}") String gatewayUrl) {
        this.restTemplate = restTemplate;
        this.gatewayUrl = gatewayUrl;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getProduct(Long id) {
        try {
            return restTemplate.getForObject(gatewayUrl + "/api/products/" + id, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getAllProducts() {
        try {
            return restTemplate.exchange(
                gatewayUrl + "/api/products",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            ).getBody();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Map<String, Object>> searchProducts(String name) {
        try {
            return restTemplate.exchange(
                gatewayUrl + "/api/products/search?name=" + name,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            ).getBody();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Map<String, Object>> getProductsByCategory(Long categoryId) {
        try {
            return restTemplate.exchange(
                gatewayUrl + "/api/products/category/" + categoryId,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            ).getBody();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Map<String, Object>> getAllCategories() {
        try {
            return restTemplate.exchange(
                gatewayUrl + "/api/products/categories",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            ).getBody();
        } catch (Exception e) {
            return List.of();
        }
    }

    public Map<String, Object> createProduct(Map<String, Object> product, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (token != null) headers.setBearerAuth(token);
            var entity = new HttpEntity<>(product, headers);
            return restTemplate.postForObject(gatewayUrl + "/api/products", entity, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> updateProduct(Long id, Map<String, Object> product, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (token != null) headers.setBearerAuth(token);
            var entity = new HttpEntity<>(product, headers);
            return restTemplate.exchange(gatewayUrl + "/api/products/" + id, HttpMethod.PUT, entity, Map.class).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteProduct(Long id, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (token != null) headers.setBearerAuth(token);
            restTemplate.exchange(gatewayUrl + "/api/products/" + id, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
        } catch (Exception e) {
            // ignore
        }
    }
}
