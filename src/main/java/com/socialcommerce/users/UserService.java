package com.socialcommerce.users;

import java.util.List;

public interface UserService {
    // TODO P1: profile CRUD, stats
    List<Long> getFollowingIds(Long userId);
}
