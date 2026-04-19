package com.socialcommerce.users.dto;

import com.socialcommerce.auth.entity.User;
import com.socialcommerce.users.entity.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {

    // from User table
    private String      uuid;
    private String      name;
    private String      email;
    private User.Role   role;
    private Boolean     isActive;

    // from UserProfile table
    private String      bio;
    private String      avatarUrl;
    private String      website;
    private String      storeName;
    private Integer     followersCount;
    private Integer     followingCount;
    private Integer     postCount;

    /** Safe factory — never expose passwordHash */
    public static UserProfileDTO from(User user, UserProfile profile) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUuid(user.getUuid());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        dto.setBio(profile.getBio());
        dto.setAvatarUrl(profile.getAvatarUrl());
        dto.setWebsite(profile.getWebsite());
        dto.setStoreName(profile.getStoreName());
        dto.setFollowersCount(profile.getFollowersCount());
        dto.setFollowingCount(profile.getFollowingCount());
        dto.setPostCount(profile.getPostCount());
        return dto;
    }
}
