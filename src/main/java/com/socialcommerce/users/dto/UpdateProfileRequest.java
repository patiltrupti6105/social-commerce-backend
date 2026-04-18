package com.socialcommerce.users.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String bio;
    private String avatarUrl;   // Cloudinary URL pasted by user
    private String website;
    private String storeName;   // only relevant if role = SELLER
}
