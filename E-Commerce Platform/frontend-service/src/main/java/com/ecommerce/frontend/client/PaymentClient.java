package com.ecommerce.frontend.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class PaymentClient {

    private final RestTemplate restTemplate;
    private final String gatewayUrl;

    public PaymentClient(RestTemplate restTemplate, @Value("${api.gateway.url}") String gatewayUrl) {
        this.restTemplate = restTemplate;
        this.gatewayUrl = gatewayUrl;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> processPayment(Long orderId, Long userId, String amount, String method, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (token != null) headers.setBearerAuth(token);
            var request = Map.of(
                "orderId", orderId,
                "userId", userId,
                "amount", amount,
                "method", method
            );
            var response = restTemplate.exchange(
                gatewayUrl + "/api/payments",
                HttpMethod.POST, new HttpEntity<>(request, headers), Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getPaymentByOrder(Long orderId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (token != null) headers.setBearerAuth(token);
            var response = restTemplate.exchange(
                gatewayUrl + "/api/payments/order/" + orderId,
                HttpMethod.GET, new HttpEntity<>(headers), Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
