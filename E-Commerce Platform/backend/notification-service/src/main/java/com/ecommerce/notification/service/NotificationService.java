package com.ecommerce.notification.service;

import com.ecommerce.notification.model.*;
import com.ecommerce.notification.repository.NotificationRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification sendNotification(NotificationRequest request) {
        Notification notification = new Notification(
            request.getUserId(),
            request.getSubject(),
            request.getMessage(),
            Notification.NotificationType.valueOf(request.getType()),
            Notification.NotificationChannel.valueOf(request.getChannel())
        );

        // Simulate sending notification
        try {
            if (notification.getChannel() == Notification.NotificationChannel.EMAIL) {
                sendEmail(notification);
            } else if (notification.getChannel() == Notification.NotificationChannel.SMS) {
                sendSms(notification);
            }
            notification.setStatus(Notification.NotificationStatus.SENT);
        } catch (Exception e) {
            notification.setStatus(Notification.NotificationStatus.FAILED);
        }

        return notificationRepository.save(notification);
    }

    @RabbitListener(queues = "notification.queue")
    public void handleOrderEvent(java.util.Map<String, Object> event) {
        System.out.println("Received order event: " + event);

        Long userId = ((Number) event.get("userId")).longValue();
        String eventType = String.valueOf(event.get("event"));
        String orderId = String.valueOf(event.get("orderId"));
        String status = String.valueOf(event.get("status"));

        Notification.NotificationType type = "ORDER_CREATED".equals(eventType)
            ? Notification.NotificationType.ORDER_CONFIRMATION
            : Notification.NotificationType.SHIPPING_UPDATE;

        NotificationRequest request = new NotificationRequest();
        request.setUserId(userId);
        request.setSubject("Order #" + orderId + " " + status);
        request.setMessage("Your order #" + orderId + " is now " + status + ".");
        request.setType(type.name());
        request.setChannel(Notification.NotificationChannel.EMAIL.name());
        sendNotification(request);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    private void sendEmail(Notification notification) {
        // Simulate email sending via SendGrid/SES
        System.out.println("Sending email to user " + notification.getUserId() +
            ": " + notification.getSubject());
    }

    private void sendSms(Notification notification) {
        // Simulate SMS sending via Twilio
        System.out.println("Sending SMS to user " + notification.getUserId() +
            ": " + notification.getMessage());
    }
}
