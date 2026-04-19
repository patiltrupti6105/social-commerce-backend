package com.socialcommerce.users;

import com.socialcommerce.auth.entity.User;
import com.socialcommerce.auth.repository.UserRepository;
import com.socialcommerce.common.exception.ResourceNotFoundException;
import com.socialcommerce.users.dto.UpdateProfileRequest;
import com.socialcommerce.users.dto.UserProfileDTO;
import com.socialcommerce.users.entity.UserProfile;
import com.socialcommerce.users.repository.FollowRepository;
import com.socialcommerce.users.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository ;
    private final UserProfileRepository userProfileRepository;
    private final FollowRepository followRepository ;

    // ─── GET MY PROFILE ──────────────────────────────────────────────────────
    @Override
    public UserProfileDTO getMyProfile(String uuid) {
        User        user    = findUserByUuid(uuid);
        UserProfile profile = findProfile(user.getId());
        return UserProfileDTO.from(user, profile);
    }

    // ─── GET ANY PUBLIC PROFILE ───────────────────────────────────────────────
    @Override
    public UserProfileDTO getProfileByUuid(String uuid) {
        User        user    = findUserByUuid(uuid);
        UserProfile profile = findProfile(user.getId());
        return UserProfileDTO.from(user, profile);
    }

    // ─── UPDATE MY PROFILE ────────────────────────────────────────────────────
    @Override
    @Transactional
    public UserProfileDTO updateProfile(String uuid, UpdateProfileRequest request) {
        User        user    = findUserByUuid(uuid);
        UserProfile profile = findProfile(user.getId());

        // Only update fields that were actually sent (null = keep existing value)
        if (request.getName()      != null) user.setName(request.getName());
        if (request.getBio()       != null) profile.setBio(request.getBio());
        if (request.getAvatarUrl() != null) profile.setAvatarUrl(request.getAvatarUrl());
        if (request.getWebsite()   != null) profile.setWebsite(request.getWebsite());
        if (request.getStoreName() != null) profile.setStoreName(request.getStoreName());

        userRepository.save(user);
        userProfileRepository.save(profile);

        return UserProfileDTO.from(user, profile);
    }

    // ─── FOLLOWING IDS (used by Person 3 FeedService) ────────────────────────
    @Override
    public List<Long> getFollowingIds(Long userId) {
        return followRepository.findFollowingIds(userId);
    }

    // ─── private helpers ─────────────────────────────────────────────────────

    private User findUserByUuid(String uuid) {
        return userRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + uuid));
    }

    private UserProfile findProfile(Long userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));
    }
}
