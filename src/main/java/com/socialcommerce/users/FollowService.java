package com.socialcommerce.users;

import java.util.List;

public interface FollowService {
    // TODO P1: follow/unfollow, followers/following pages; keep contract for P3 FeedService
    List<Long> getFollowingIds(Long userId);
}
