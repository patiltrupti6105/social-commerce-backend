package com.socialcommerce.notifications;

import com.socialcommerce.notifications.document.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationEmitterRegistry emitterRegistry;

    @GetMapping("/{userId}")
    public List<Notification> getUserNotifications(@PathVariable Long userId) {
        return notificationService.getUserNotifications(userId);
    }

    @PutMapping("/read/{notificationId}")
    public String markAsRead(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return "Notification marked as read";
    }

    @GetMapping("/subscribe/{userId}")
    public SseEmitter subscribe(@PathVariable Long userId) {
        return emitterRegistry.addEmitter(userId);
    }
}