package com.socialcommerce.users;

import com.socialcommerce.users.dto.UpdateProfileRequest;
import com.socialcommerce.users.dto.UserProfileDTO;

import java.util.List;

public interface UserService {

    UserProfileDTO getMyProfile(String uuid);
    UserProfileDTO getProfileByUuid(String uuid);
    UserProfileDTO updateProfile(String uuid, UpdateProfileRequest request);

    // Called by Person 3's FeedService to build the personalized feed
    List<Long> getFollowingIds(Long userId);
}
