package com.socialcommerce.users.controller;

import com.socialcommerce.common.response.ApiResponse;
import com.socialcommerce.users.dto.UpdateProfileRequest;
import com.socialcommerce.users.dto.UserProfileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.socialcommerce.users.UserService;
import com.socialcommerce.users.FollowService;

/**
 * Handles ALL /api/v1/users/** endpoints.
 *
 * FIX: Previously UserController and FollowController both had
 * @RequestMapping("/api/v1/users") which causes Spring to crash on startup.
 * Solution: one controller handles everything under /api/v1/users/**.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService ;
    private final FollowService followService ;

    // ── PROFILE ENDPOINTS ─────────────────────────────────────────────────────

    /**
     * GET /api/v1/users/me
     * Returns the logged-in user's full profile.
     * Requires: Authorization: Bearer <token>
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getMe() {
        String uuid = currentUuid();
        return ResponseEntity.ok(ApiResponse.success(
                userService.getMyProfile(uuid), "Profile fetched"));
    }

    /**
     * PUT /api/v1/users/me
     * Update your own profile fields (send only what you want to change).
     * Body: { "name":"New Name", "bio":"Hello", "avatarUrl":"https://...", "website":"...", "storeName":"..." }
     * Requires: Authorization: Bearer <token>
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateMe(
            @RequestBody UpdateProfileRequest request) {
        String uuid = currentUuid();
        return ResponseEntity.ok(ApiResponse.success(
                userService.updateProfile(uuid, request), "Profile updated"));
    }

    /**
     * GET /api/v1/users/{uuid}
     * Public — returns any user's profile by their UUID.
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getUser(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success(
                userService.getProfileByUuid(uuid), "User fetched"));
    }

    // ── FOLLOW ENDPOINTS ──────────────────────────────────────────────────────

    /**
     * POST /api/v1/users/{uuid}/follow
     * Follow another user.
     * Requires: Authorization: Bearer <token>
     */
    @PostMapping("/{uuid}/follow")
    public ResponseEntity<ApiResponse<?>> follow(@PathVariable String uuid) {
        followService.follow(currentUuid(), uuid);
        return ResponseEntity.ok(ApiResponse.success(null, "Followed successfully"));
    }

    /**
     * DELETE /api/v1/users/{uuid}/follow
     * Unfollow a user.
     * Requires: Authorization: Bearer <token>
     */
    @DeleteMapping("/{uuid}/follow")
    public ResponseEntity<ApiResponse<?>> unfollow(@PathVariable String uuid) {
        followService.unfollow(currentUuid(), uuid);
        return ResponseEntity.ok(ApiResponse.success(null, "Unfollowed successfully"));
    }

    /**
     * GET /api/v1/users/{uuid}/followers
     * Returns list of users who follow this person.
     * Public.
     */
    @GetMapping("/{uuid}/followers")
    public ResponseEntity<ApiResponse<List<UserProfileDTO>>> getFollowers(
            @PathVariable String uuid,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(ApiResponse.success(
                followService.getFollowers(uuid), "Followers fetched"));
    }

    /**
     * GET /api/v1/users/{uuid}/following
     * Returns list of users this person follows.
     * Public.
     */
    @GetMapping("/{uuid}/following")
    public ResponseEntity<ApiResponse<List<UserProfileDTO>>> getFollowing(
            @PathVariable String uuid,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(ApiResponse.success(
                followService.getFollowing(uuid), "Following fetched"));
    }

    // ── HELPER ────────────────────────────────────────────────────────────────

    /** JwtAuthFilter sets the UUID as the principal — pull it out here. */
    private String currentUuid() {
        return (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
}
