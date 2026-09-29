package com.socialcommerce.social;

import com.socialcommerce.social.document.Comment;
import com.socialcommerce.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Comment>> addComment(
            @PathVariable String postId,
            @RequestBody Comment comment) {
        comment.setAuthorId(currentUserId());
        return ResponseEntity.ok(ApiResponse.success(commentService.addComment(postId, comment)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Comment>>> getComments(@PathVariable String postId) {
        return ResponseEntity.ok(ApiResponse.success(commentService.getComments(postId)));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<?>> deleteComment(
            @PathVariable String postId,
            @PathVariable String commentId) {
        commentService.deleteComment(commentId, postId);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted"));
    }

    private String currentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
