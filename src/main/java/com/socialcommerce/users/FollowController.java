package com.socialcommerce.users;

import com.socialcommerce.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class FollowController {

    // TODO P1: inject FollowService

    @PostMapping("/{uuid}/follow")
    public ResponseEntity<ApiResponse<?>> follow(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: follow user"));
    }

    @DeleteMapping("/{uuid}/follow")
    public ResponseEntity<ApiResponse<?>> unfollow(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: unfollow user"));
    }

    @GetMapping("/{uuid}/followers")
    public ResponseEntity<ApiResponse<?>> getFollowers(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: paginated followers"));
    }

    @GetMapping("/{uuid}/following")
    public ResponseEntity<ApiResponse<?>> getFollowing(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: paginated following"));
    }
}
