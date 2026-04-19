package com.socialcommerce.social;

import com.socialcommerce.social.document.Post;
import com.socialcommerce.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<Post>> createPost(@RequestBody Post post) {
        String userId = currentUserId();
        post.setAuthorId(userId);
        return ResponseEntity.ok(ApiResponse.success(postService.createPost(post), "Post created"));
    }

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<Page<Post>>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String userId = currentUserId();
        return ResponseEntity.ok(ApiResponse.success(postService.getFeed(userId, page, size)));
    }

    @GetMapping("/explore")
    public ResponseEntity<ApiResponse<List<Post>>> explore() {
        return ResponseEntity.ok(ApiResponse.success(postService.getExplorePosts()));
    }

    @PutMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Post>> likePost(@PathVariable String postId) {
        return ResponseEntity.ok(ApiResponse.success(postService.toggleLike(postId, currentUserId())));
    }

    @GetMapping("/user/{authorId}")
    public ResponseEntity<ApiResponse<Page<Post>>> getUserPosts(
            @PathVariable String authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(postService.getUserPosts(authorId, page, size)));
    }

    @PutMapping("/{postId}/report")
    public ResponseEntity<ApiResponse<Post>> reportPost(@PathVariable String postId) {
        return ResponseEntity.ok(ApiResponse.success(postService.reportPost(postId)));
    }

    @GetMapping("/reported")
    public ResponseEntity<ApiResponse<List<Post>>> getReportedPosts() {
        return ResponseEntity.ok(ApiResponse.success(postService.getReportedPosts()));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> deletePost(@PathVariable String postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(ApiResponse.success(null, "Post deleted"));
    }
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<Post>> getPost(@PathVariable String postId) {
        return ResponseEntity.ok(ApiResponse.success(postService.getPostById(postId)));
    }
    private String currentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
