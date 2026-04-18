package com.socialcommerce.social;

import com.socialcommerce.social.document.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/{postId}")
    public Comment addComment(@PathVariable String postId,
                              @RequestBody Comment comment) {
        return commentService.addComment(postId, comment);
    }

    @GetMapping("/{postId}")
    public List<Comment> getComments(@PathVariable String postId) {
        return commentService.getComments(postId);
    }

    @DeleteMapping("/{commentId}/{postId}")
    public String deleteComment(@PathVariable String commentId,
                               @PathVariable String postId) {
        commentService.deleteComment(commentId, postId);
        return "Deleted";
    }
}