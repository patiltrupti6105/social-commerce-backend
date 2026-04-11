package com.socialcommerce.notifications;

import com.socialcommerce.notifications.document.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
