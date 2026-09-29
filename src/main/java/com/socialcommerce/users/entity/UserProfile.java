package com.socialcommerce.users.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_profiles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {

    @Id
    private Long userId;

    private String bio;
    private String avatarUrl;
    private String website;
    private String storeName;
    private Integer followersCount = 0;
    private Integer followingCount = 0;
    private Integer postCount = 0;
}
