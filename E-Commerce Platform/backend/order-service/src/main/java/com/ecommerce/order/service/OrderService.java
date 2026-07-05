package com.ecommerce.order.service;

import com.ecommerce.order.config.RabbitMQConfig;
import com.ecommerce.order.model.*;
import com.ecommerce.order.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    public OrderService(OrderRepository orderRepository, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    private void publishOrderEvent(Order order, String event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_QUEUE, Map.of(
                "event", event,
                "orderId", order.getId(),
                "userId", order.getUserId(),
                "status", order.getStatus().name()
            ));
        } catch (Exception e) {
            // ponytail: notification is best-effort; order must not fail if broker is down
            System.err.println("Failed to publish order event: " + e.getMessage());
        }
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        BigDecimal total = request.getItems().stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order(request.getUserId(), total, request.getShippingAddress());

        for (CreateOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            OrderItem item = new OrderItem(
                itemReq.getProductId(),
                itemReq.getProductName(),
                itemReq.getPrice(),
                itemReq.getQuantity()
            );
            order.addItem(item);
        }

        Order saved = orderRepository.save(order);
        publishOrderEvent(saved, "ORDER_CREATED");
        return saved;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        Order saved = orderRepository.save(order);
        publishOrderEvent(saved, "ORDER_STATUS_UPDATED");
        return saved;
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() == Order.OrderStatus.SHIPPED ||
            order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel order that has been shipped or delivered");
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        publishOrderEvent(saved, "ORDER_CANCELLED");
        return saved;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
