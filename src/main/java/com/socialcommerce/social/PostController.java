package com.socialcommerce.social;

import com.socialcommerce.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    // TODO P3: inject PostService, FeedService

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<?>> getFeed(@RequestParam(required = false) String cursor) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: personalized feed"));
    }

    @GetMapping("/explore")
    public ResponseEntity<ApiResponse<?>> getExplore() {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: trending posts"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createPost(@RequestBody Object request) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: create post"));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> getPost(@PathVariable String postId) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: get post by id"));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> deletePost(@PathVariable String postId) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: delete post (owner or ADMIN)"));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<?>> likePost(@PathVariable String postId) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: toggle like"));
    }

    @PostMapping("/{postId}/report")
    public ResponseEntity<ApiResponse<?>> reportPost(@PathVariable String postId) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: report post"));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<?>> addComment(@PathVariable String postId, @RequestBody Object request) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: add comment"));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<?>> getComments(@PathVariable String postId) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: get comments"));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<?>> deleteComment(@PathVariable String postId, @PathVariable String commentId) {
        return ResponseEntity.ok(ApiResponse.success(null, "TODO: delete comment (owner or ADMIN)"));
    }
}
