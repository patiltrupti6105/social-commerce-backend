package com.socialcommerce.notifications;

import com.socialcommerce.auth.repository.UserRepository;
import com.socialcommerce.config.JwtUtil;
import com.socialcommerce.notifications.document.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationEmitterRegistry emitterRegistry;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${app.pagination.notification-page-size:30}")
    private int notificationPageSize;

    public Notification createNotification(Long userId, String message, String type) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setMessage(message);
        n.setType(type);
        n.setCreatedAt(LocalDateTime.now());
        n.setRead(false);
        Notification saved = notificationRepository.save(n);
        emitterRegistry.sendNotification(userId, saved);
        return saved;
    }

    public Page<Notification> getUserNotifications(Long userId, int page) {
        PageRequest pr = PageRequest.of(page, notificationPageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pr);
    }

    /** @deprecated Use getUserNotifications(userId, page) for paginated access */
    @Deprecated
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    public void markAsRead(String notificationId) {
        Notification n = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setRead(true);
        notificationRepository.save(n);
    }

    /** Validates a raw JWT token string (no "Bearer " prefix) and returns the UUID claim. */
    public String validateTokenAndGetUuid(String token) {
        if (token == null || !jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid or missing token");
        }
        return jwtUtil.extractUserId(token);
    }

    /** Looks up the numeric user ID from a UUID. */
    public Long getUserIdByUuid(String uuid) {
        return userRepository.findByUuid(uuid)
            .orElseThrow(() -> new RuntimeException("User not found"))
            .getId();
    }
}
