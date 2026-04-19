package com.socialcommerce.notifications;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class NotificationEmitterRegistry {

    // Store emitters for each user
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // Create connection for user
    public SseEmitter addEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        return emitter;
    }

    // Send notification to user
    public void sendNotification(Long userId, Object notification) {
        SseEmitter emitter = emitters.get(userId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(notification));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }
}