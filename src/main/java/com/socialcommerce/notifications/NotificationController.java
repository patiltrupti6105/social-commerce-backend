package com.socialcommerce.notifications;

import com.socialcommerce.notifications.document.Notification;
import com.socialcommerce.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationEmitterRegistry emitterRegistry;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Notification>>> getUserNotifications(
            @RequestParam(defaultValue = "0") int page) {
        Long userId = currentUserId();
        return ResponseEntity.ok(ApiResponse.success(notificationService.getUserNotifications(userId, page)));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<?>> markAsRead(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success(null, "Marked as read"));
    }

    @GetMapping("/subscribe")
    public SseEmitter subscribe(
            @RequestParam(required = false) String token) {
        Long userId;
        try {
            String uuid = notificationService.validateTokenAndGetUuid(token);
            userId = notificationService.getUserIdByUuid(uuid);
        } catch (Exception e) {
            SseEmitter emitter = new SseEmitter(0L);
            emitter.completeWithError(new RuntimeException("Unauthorized"));
            return emitter;
        }
        return emitterRegistry.addEmitter(userId);
    }

    private Long currentUserId() {
        return Long.parseLong((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
