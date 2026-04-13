package com.socialcommerce.notifications;

import com.socialcommerce.notifications.document.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.socialcommerce.notifications.NotificationType;
@Service
public interface NotificationService {
	
    void createNotification(String recipientId,
                            Notification.NotificationType type,
                            String actorId,
                            String actorName,
                            String entityId,
                            String message);

    Page<Notification> getNotifications(Long userId, Pageable pageable);

    void markAsRead(String notificationId, Long userId);

    void markAllAsRead(Long userId);

    long getUnreadCount(Long userId);
}
