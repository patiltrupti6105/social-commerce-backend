package com.socialcommerce.social;

import com.socialcommerce.common.BaseController;
import com.socialcommerce.social.document.Post;
import com.socialcommerce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController extends BaseController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<Post>> createPost(@Valid @RequestBody Post post) {
        // Store UUID as authorId - consistent with JWT principal
        post.setAuthorId(currentUserUuid());
        return ResponseEntity.ok(ApiResponse.success(postService.createPost(post), "Post created"));
    }

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<Page<Post>>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(postService.getFeed(currentUserUuid(), page, size)));
    }

    @GetMapping("/explore")
    public ResponseEntity<ApiResponse<List<Post>>> explore() {
        return ResponseEntity.ok(ApiResponse.success(postService.getExplorePosts()));
    }

    @PutMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Post>> likePost(@PathVariable String postId) {
        return ResponseEntity.ok(ApiResponse.success(postService.toggleLike(postId, currentUserUuid())));
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
}
