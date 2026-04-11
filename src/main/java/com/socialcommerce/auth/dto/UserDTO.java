package com.socialcommerce.auth.dto;

import com.socialcommerce.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String uuid;
    private String name;
    private String email;
    private User.Role role;

    public static UserDTO from(User user) {
        return new UserDTO(
            user.getUuid(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }
}
