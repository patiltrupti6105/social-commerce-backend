package com.socialcommerce.common;

import com.socialcommerce.auth.entity.User;
import com.socialcommerce.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Base controller providing common authentication utilities.
 * All controllers should extend this class to access current user information.
 */
public abstract class BaseController {

    @Autowired
    protected UserRepository userRepository;

    /**
     * Returns the UUID of the currently authenticated user.
     * This is the primary identifier used across the system.
     * 
     * @return UUID string (e.g., "550e8400-e29b-41d4-a716-446655440000")
     */
    protected String currentUserUuid() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Returns the numeric database ID of the currently authenticated user.
     * Used for MySQL foreign key relationships.
     * 
     * @return numeric user ID
     * @throws RuntimeException if user not found (should never happen for authenticated requests)
     */
    protected Long currentUserNumericId() {
        String uuid = currentUserUuid();
        return userRepository.findByUuid(uuid)
            .map(User::getId)
            .orElseThrow(() -> new RuntimeException("User not found for UUID: " + uuid));
    }

    /**
     * Returns the full User entity of the currently authenticated user.
     * Use sparingly - prefer currentUserUuid() or currentUserNumericId() for better performance.
     * 
     * @return authenticated User entity
     * @throws RuntimeException if user not found (should never happen for authenticated requests)
     */
    protected User currentUser() {
        String uuid = currentUserUuid();
        return userRepository.findByUuid(uuid)
            .orElseThrow(() -> new RuntimeException("User not found for UUID: " + uuid));
    }
}
