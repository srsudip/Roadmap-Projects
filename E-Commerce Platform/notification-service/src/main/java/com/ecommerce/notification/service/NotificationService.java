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
    public void handleOrderEvent(String message) {
        System.out.println("Received order event: " + message);
        // Process order event and send appropriate notification
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
