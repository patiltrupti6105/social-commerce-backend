package com.socialcommerce.social;

import com.socialcommerce.social.document.Comment;
import com.socialcommerce.notifications.NotificationService;
import com.socialcommerce.social.document.Post;
import com.socialcommerce.social.repository.CommentRepository;
import com.socialcommerce.social.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    public Comment addComment(String postId, Comment comment) {
        comment.setPostId(postId);
        comment.setCreatedAt(LocalDateTime.now());
        Comment saved = commentRepository.save(comment);

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        // Notify actual post author
        if (post.getAuthorId() != null && !post.getAuthorId().equals(comment.getAuthorId())) {
            try {
                notificationService.createNotification(
                    Long.parseLong(post.getAuthorId()),
                    "New comment on your post",
                    "COMMENT"
                );
            } catch (Exception ignored) {}
        }
        return saved;
    }

    public List<Comment> getComments(String postId) {
        return commentRepository.findByPostId(postId);
    }

    public void deleteComment(String commentId, String postId) {
        commentRepository.deleteById(commentId);
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setCommentsCount(Math.max(0, post.getCommentsCount() - 1));
        postRepository.save(post);
    }
}
