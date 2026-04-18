package com.socialcommerce.social;

import com.socialcommerce.social.document.Post;

import com.socialcommerce.notifications.NotificationService;
import com.socialcommerce.social.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private NotificationService notificationService;

    // ✅ Create a post
    public Post createPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setReported(false);
        return postRepository.save(post);
    }
    
    public Post toggleLike(String postId, String userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // ✅ Null safety
        if (post.getLikedByUserIds() == null) {
            post.setLikedByUserIds(new ArrayList<>());
        }

        if (post.getLikedByUserIds().contains(userId)) {
            post.getLikedByUserIds().remove(userId);
            post.setLikesCount(post.getLikesCount() - 1);
        } else {
            post.getLikedByUserIds().add(userId);
            post.setLikesCount(post.getLikesCount() + 1);

            // ✅ Only when LIKE happens
            notificationService.createNotification(
                    1L, // TODO: replace later
                    "Someone liked your post",
                    "LIKE"
            );
        }

        return postRepository.save(post);
    }

    // ✅ Get feed
    public Page<Post> getFeed(List<String> authorIds, int page, int size) {
        return postRepository.findByAuthorIdIn(authorIds, PageRequest.of(page, size));
    }

    // ✅ Get user posts
    public Page<Post> getUserPosts(String authorId, int page, int size) {
        return postRepository.findByAuthorId(authorId, PageRequest.of(page, size));
    }

    // ✅ Report post
    public Post reportPost(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setReported(true);
        return postRepository.save(post);
    }
    
    public List<Post> getExplorePosts() {
        return postRepository.findTop10ByOrderByLikesCountDesc();
    }

    // ✅ Get reported posts
    public List<Post> getReportedPosts() {
        return postRepository.findByIsReportedTrue();
    }

    // ✅ Delete post
    public void deletePost(String postId) {
        postRepository.deleteById(postId);
    }
}