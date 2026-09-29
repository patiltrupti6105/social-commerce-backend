package com.socialcommerce.users;

import com.socialcommerce.users.dto.UserProfileDTO;

import java.util.List;

public interface FollowService {

    void follow(String currentUserUuid, String targetUuid);
    void unfollow(String currentUserUuid, String targetUuid);

    // Paginated follower / following lists
    List<UserProfileDTO> getFollowers(String uuid);
    List<UserProfileDTO> getFollowing(String uuid);

    // Called by Person 3's FeedService
    List<Long> getFollowingIds(Long userId);
}
