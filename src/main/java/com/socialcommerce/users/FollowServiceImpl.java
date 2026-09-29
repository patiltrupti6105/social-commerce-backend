package com.socialcommerce.users;

import com.socialcommerce.auth.entity.User;
import com.socialcommerce.auth.repository.UserRepository;
import com.socialcommerce.common.exception.ResourceNotFoundException;
import com.socialcommerce.users.dto.UserProfileDTO;
import com.socialcommerce.users.entity.Follow;
import com.socialcommerce.users.entity.UserProfile;
import com.socialcommerce.users.repository.FollowRepository;
import com.socialcommerce.users.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository ;
    private final UserProfileRepository userProfileRepository ;
    private final FollowRepository followRepository;

    // ─── FOLLOW ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void follow(String currentUserUuid, String targetUuid) {
        User currentUser = findUserByUuid(currentUserUuid);
        User targetUser  = findUserByUuid(targetUuid);

        if (currentUser.getId().equals(targetUser.getId())) {
            throw new IllegalArgumentException("You cannot follow yourself.");
        }

        // Silently ignore if already following
        if (followRepository.existsByFollowerIdAndFolloweeId(
                currentUser.getId(), targetUser.getId())) {
            return;
        }

        // Save follow record
        Follow follow = new Follow();
        follow.setFollowerId(currentUser.getId());
        follow.setFolloweeId(targetUser.getId());
        followRepository.save(follow);

        // Update counters on both profiles
        UserProfile myProfile     = findProfile(currentUser.getId());
        UserProfile targetProfile = findProfile(targetUser.getId());

        myProfile.setFollowingCount(myProfile.getFollowingCount() + 1);
        targetProfile.setFollowersCount(targetProfile.getFollowersCount() + 1);

        userProfileRepository.save(myProfile);
        userProfileRepository.save(targetProfile);
    }

    // ─── UNFOLLOW ─────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void unfollow(String currentUserUuid, String targetUuid) {
        User currentUser = findUserByUuid(currentUserUuid);
        User targetUser  = findUserByUuid(targetUuid);

        // Silently ignore if not following
        if (!followRepository.existsByFollowerIdAndFolloweeId(
                currentUser.getId(), targetUser.getId())) {
            return;
        }

        followRepository.deleteByFollowerIdAndFolloweeId(
                currentUser.getId(), targetUser.getId());

        // Decrement counters (floor at 0)
        UserProfile myProfile     = findProfile(currentUser.getId());
        UserProfile targetProfile = findProfile(targetUser.getId());

        myProfile.setFollowingCount(Math.max(0, myProfile.getFollowingCount() - 1));
        targetProfile.setFollowersCount(Math.max(0, targetProfile.getFollowersCount() - 1));

        userProfileRepository.save(myProfile);
        userProfileRepository.save(targetProfile);
    }

    // ─── GET FOLLOWERS LIST ───────────────────────────────────────────────────
    @Override
    public List<UserProfileDTO> getFollowers(String uuid) {
        User user = findUserByUuid(uuid);
        List<Long> followerIds = followRepository.findFollowerIds(user.getId());
        return buildProfileList(followerIds);
    }

    // ─── GET FOLLOWING LIST ───────────────────────────────────────────────────
    @Override
    public List<UserProfileDTO> getFollowing(String uuid) {
        User user = findUserByUuid(uuid);
        List<Long> followingIds = followRepository.findFollowingIds(user.getId());
        return buildProfileList(followingIds);
    }

    // ─── FOLLOWING IDS (Person 3 FeedService uses this) ──────────────────────
    @Override
    public List<Long> getFollowingIds(Long userId) {
        return followRepository.findFollowingIds(userId);
    }

    // ─── private helpers ─────────────────────────────────────────────────────

    private List<UserProfileDTO> buildProfileList(List<Long> userIds) {
        return userIds.stream().map(id -> {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
            UserProfile profile = findProfile(id);
            return UserProfileDTO.from(user, profile);
        }).collect(Collectors.toList());
    }

    private User findUserByUuid(String uuid) {
        return userRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + uuid));
    }

    private UserProfile findProfile(Long userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profile not found for userId: " + userId));
    }
}
