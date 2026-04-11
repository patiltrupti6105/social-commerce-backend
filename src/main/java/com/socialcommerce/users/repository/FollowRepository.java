package com.socialcommerce.users.repository;

import com.socialcommerce.users.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
    void deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    // Person 3 needs this for feed generation
    @Query("SELECT f.followeeId FROM Follow f WHERE f.followerId = :userId")
    List<Long> findFollowingIds(Long userId);
}
