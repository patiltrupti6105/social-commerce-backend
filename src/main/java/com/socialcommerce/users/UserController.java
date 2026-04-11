package com.socialcommerce.users;

import com.socialcommerce.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    // TODO P1: inject UserService

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getMe() {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: return logged in user profile"));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<?>> updateMe(@RequestBody Object request) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: update profile"));
    }

    @GetMapping("/{uuid}/posts")
    public ResponseEntity<ApiResponse<?>> getPostsByUser(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: all posts by user (public)"));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> getUserByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: return user by uuid"));
    }
}
