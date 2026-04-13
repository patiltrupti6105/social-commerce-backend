package com.socialcommerce.admin;

import com.socialcommerce.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
// @PreAuthorize("hasRole('ADMIN')") // TODO P1: uncomment once roles on SecurityContext
public class AdminController {

    // TODO P1: inject AdminService, ProductService, PostService

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<?>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success("TODO: list all users"));
    }

    @PutMapping("/users/{uuid}/disable")
    public ResponseEntity<ApiResponse<?>> disableUser(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success("TODO: disable user"));
    }

    @PutMapping("/users/{uuid}/enable")
    public ResponseEntity<ApiResponse<?>> enableUser(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success("TODO: enable user"));
    }

    @PutMapping("/users/{uuid}/grant-seller")
    public ResponseEntity<ApiResponse<?>> grantSeller(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success("TODO: grant seller role"));
    }

    @GetMapping("/products/pending")
    public ResponseEntity<ApiResponse<?>> getPendingProducts() {
        return ResponseEntity.ok(ApiResponse.success( "TODO: get pending products (P2 ProductService)"));
    }

    @PostMapping("/products/{id}/approve")
    public ResponseEntity<ApiResponse<?>> approveProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success( "TODO: approve product (P2)"));
    }

    @PostMapping("/products/{id}/reject")
    public ResponseEntity<ApiResponse<?>> rejectProduct(@PathVariable Long id, @RequestBody Object request) {
        return ResponseEntity.ok(ApiResponse.success( "TODO: reject product (P2)"));
    }

    @GetMapping("/posts/reported")
    public ResponseEntity<ApiResponse<?>> getReportedPosts() {
        return ResponseEntity.ok(ApiResponse.success( "TODO: get reported posts (P3)"));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<?>> deletePost(@PathVariable String postId) {
        return ResponseEntity.ok(ApiResponse.success( "TODO: delete post (P3)"));
    }
}
