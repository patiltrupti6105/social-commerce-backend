package com.socialcommerce.notifications;

import com.socialcommerce.notifications.document.Notification;
import com.socialcommerce.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationEmitterRegistry emitterRegistry;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getUserNotifications() {
        Long userId = currentUserId();
        return ResponseEntity.ok(ApiResponse.success(notificationService.getUserNotifications(userId)));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<?>> markAsRead(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success(null, "Marked as read"));
    }

    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        Long userId = currentUserId();
        return emitterRegistry.addEmitter(userId);
    }

    private Long currentUserId() {
        return Long.parseLong((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
