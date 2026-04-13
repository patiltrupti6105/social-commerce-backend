package com.socialcommerce.notifications;

import com.socialcommerce.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    // TODO P3: inject NotificationService, NotificationEmitterRegistry

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success( "TODO: get notifications (Pageable)"));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<?>> getUnreadCount() {
        return ResponseEntity.ok(ApiResponse.success("TODO: get unread count"));
    }

    @GetMapping("/stream")
    public SseEmitter streamNotifications() {
        SseEmitter emitter = new SseEmitter(30_000L);
        // TODO P3: resolve current user id and registry.register(userId, emitter)
        emitter.complete();
        return emitter;
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<?>> markRead(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success( "TODO: mark as read"));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<?>> markAllRead() {
        return ResponseEntity.ok(ApiResponse.success( "TODO: mark all as read"));
    }
}
