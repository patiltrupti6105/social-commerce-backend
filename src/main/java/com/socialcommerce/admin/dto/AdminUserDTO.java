package com.socialcommerce.admin.dto;

import com.socialcommerce.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserDTO {

    private Long            id;
    private String          uuid;
    private String          name;
    private String          email;
    private User.Role       role;
    private Boolean         isActive;
    private LocalDateTime   createdAt;

    /** NEVER include passwordHash in admin responses */
    public static AdminUserDTO from(User user) {
        return new AdminUserDTO(
                user.getId(),
                user.getUuid(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getIsActive(),
                user.getCreatedAt()
        );
    }
}
