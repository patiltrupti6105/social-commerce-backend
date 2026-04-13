package com.socialcommerce.auth.dto;

import com.socialcommerce.auth.entity.User;

public class UserDTO {
    private String uuid;
    private String name;
    private String email;
    private User.Role role;

    public UserDTO() {
    }

    public UserDTO(String uuid, String name, String email, User.Role role) {
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public static UserDTO from(User user) {
        return new UserDTO(
            user.getUuid(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }
}
