package com.socialcommerce.social;

import com.socialcommerce.social.document.Comment;
import com.socialcommerce.notifications.NotificationService;
import com.socialcommerce.social.document.Post;
import com.socialcommerce.social.repository.CommentRepository;
import com.socialcommerce.social.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private NotificationService notificationService;

    public Comment addComment(String postId, Comment comment) {
        comment.setPostId(postId);
        comment.setCreatedAt(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);

        // increment comment count
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);
        
        notificationService.createNotification(
                1L, // replace with actual post owner
                "New Comment",
                "COMMENT"
        );

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