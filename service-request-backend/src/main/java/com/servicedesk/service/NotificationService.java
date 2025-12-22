package com.servicedesk.service;

import com.servicedesk.entity.Notification;
import com.servicedesk.entity.ServiceRequest;
import com.servicedesk.entity.User;
import com.servicedesk.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Notification Service
 * Business logic for notification management
 */
@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Create notification
     */
    public Notification createNotification(User user, String title, String message,
            Notification.NotificationType type, ServiceRequest request) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRequest(request);
        notification.setIsRead(false);
        return notificationRepository.save(notification);
    }

    /**
     * Get user notifications
     */
    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Get unread notifications count
     */
    public Long getUnreadNotificationsCount(Long userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    /**
     * Mark notification as read
     */
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    /**
     * Mark all notifications as read for user
     */
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsRead(userId, false);
        notifications.forEach(notification -> {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(notifications);
    }

    /**
     * Delete notification
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    /**
     * Send request created notification
     */
    public void sendRequestCreatedNotification(ServiceRequest request) {
        String title = "Service Request Created";
        String message = String.format("Your service request %s has been created successfully.",
                request.getTicketId());
        createNotification(request.getRequester(), title, message,
                Notification.NotificationType.SUCCESS, request);
    }

    /**
     * Send status change notification
     */
    public void sendStatusChangeNotification(ServiceRequest request) {
        String title = "Request Status Updated";
        String message = String.format("Your service request %s status has been updated to %s.",
                request.getTicketId(), request.getStatus());
        createNotification(request.getRequester(), title, message,
                Notification.NotificationType.INFO, request);
    }

    /**
     * Send assignment notification
     */
    public void sendAssignmentNotification(ServiceRequest request) {
        if (request.getAssignedTo() != null) {
            String title = "New Task Assigned";
            String message = String.format("Service request %s has been assigned to you.",
                    request.getTicketId());
            createNotification(request.getAssignedTo(), title, message,
                    Notification.NotificationType.INFO, request);
        }
    }

    /**
     * Send SLA breach notification
     */
    public void sendSLABreachNotification(ServiceRequest request, String breachType) {
        String title = "SLA Breach Alert";
        String message = String.format("Service request %s has breached %s SLA.",
                request.getTicketId(), breachType);
        createNotification(request.getRequester(), title, message,
                Notification.NotificationType.WARNING, request);

        if (request.getAssignedTo() != null) {
            createNotification(request.getAssignedTo(), title, message,
                    Notification.NotificationType.WARNING, request);
        }
    }
}
