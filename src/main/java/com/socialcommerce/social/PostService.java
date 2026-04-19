package com.socialcommerce.social;

import com.socialcommerce.social.document.Post;
import com.socialcommerce.notifications.NotificationService;
import com.socialcommerce.social.repository.PostRepository;
import com.socialcommerce.users.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final NotificationService notificationService;
    private final FollowService followService;

    public Post createPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setReported(false);
        return postRepository.save(post);
    }
    public Post getPostById(String postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new RuntimeException("Post not found"));
    }
    public Post toggleLike(String postId, String userId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        if (post.getLikedByUserIds() == null) post.setLikedByUserIds(new ArrayList<>());

        if (post.getLikedByUserIds().contains(userId)) {
            post.getLikedByUserIds().remove(userId);
            post.setLikesCount(post.getLikesCount() - 1);
        } else {
            post.getLikedByUserIds().add(userId);
            post.setLikesCount(post.getLikesCount() + 1);
            // Notify actual post author
            if (post.getAuthorId() != null && !post.getAuthorId().equals(userId)) {
                try {
                    notificationService.createNotification(
                        Long.parseLong(post.getAuthorId()),
                        "Someone liked your post",
                        "LIKE"
                    );
                } catch (Exception ignored) {}
            }
        }
        return postRepository.save(post);
    }

    public Page<Post> getFeed(String userId, int page, int size) {
        List<Long> followingIds = followService.getFollowingIds(Long.parseLong(userId));
        List<String> authorIds = followingIds.stream().map(String::valueOf).collect(Collectors.toList());
        authorIds.add(userId); // include own posts
        return postRepository.findByAuthorIdIn(authorIds,
            PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Page<Post> getUserPosts(String authorId, int page, int size) {
        return postRepository.findByAuthorId(authorId,
            PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Post reportPost(String postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setReported(true);
        return postRepository.save(post);
    }

    public List<Post> getExplorePosts() {
        return postRepository.findTop10ByOrderByLikesCountDesc();
    }

    public List<Post> getReportedPosts() {
        return postRepository.findByIsReportedTrue();
    }

    public void deletePost(String postId) {
        postRepository.deleteById(postId);
    }
}
