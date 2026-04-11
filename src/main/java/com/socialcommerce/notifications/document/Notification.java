package com.socialcommerce.notifications.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    private String id;

    private String recipientId;
    private NotificationType type;
    private String actorId;
    private String actorName;
    private String entityId;
    private String entityType;
    private String message;
    private Boolean isRead = false;
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum NotificationType {
        POST_LIKE, POST_COMMENT, NEW_FOLLOWER, ORDER_PLACED, ORDER_SHIPPED, ORDER_DELIVERED
    }
}
