package com.socialcommerce.social;

import com.socialcommerce.auth.entity.User;
import com.socialcommerce.auth.repository.UserRepository;
import com.socialcommerce.catalog.repository.ProductRepository;
import com.socialcommerce.social.document.Post;
import com.socialcommerce.notifications.NotificationService;
import com.socialcommerce.social.repository.PostRepository;
import com.socialcommerce.users.FollowService;
import com.socialcommerce.users.entity.UserProfile;
import com.socialcommerce.users.repository.UserProfileRepository;
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
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ProductRepository productRepository;

    public Post createPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setReported(false);

        // Denormalize author info so feed queries don't need a JOIN
        try {
            userRepository.findByUuid(post.getAuthorId()).ifPresent(user -> {
                post.setAuthorName(user.getName());
                userProfileRepository.findById(user.getId()).ifPresent(profile ->
                    post.setAuthorAvatarUrl(profile.getAvatarUrl()));
            });
        } catch (Exception ignored) {}

        // Denormalize linked product snapshot so feed renders without MySQL round-trip
        if (post.getLinkedProductId() != null) {
            try {
                productRepository.findById(post.getLinkedProductId()).ifPresent(product -> {
                    post.setLinkedProductTitle(product.getTitle());
                    post.setLinkedProductPrice(product.getPrice().doubleValue());
                    if (!product.getImages().isEmpty()) {
                        post.setLinkedProductImageUrl(product.getImages().iterator().next().getImageUrl());
                    }
                });
            } catch (Exception ignored) {
                // Non-fatal — post saves without product snapshot
            }
        }

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
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
        } else {
            post.getLikedByUserIds().add(userId);
            post.setLikesCount(post.getLikesCount() + 1);
            if (post.getAuthorId() != null && !post.getAuthorId().equals(userId)) {
                try {
                    Long authorNumericId = resolveUuidToNumericId(post.getAuthorId());
                    notificationService.createNotification(
                        authorNumericId,
                        "Someone liked your post",
                        "LIKE"
                    );
                } catch (Exception ignored) {}
            }
        }
        return postRepository.save(post);
    }

    public Page<Post> getFeed(String userId, int page, int size) {
        // userId is now UUID from JwtAuthFilter (after migration).
        // Convert UUID to numeric ID for MySQL follow queries.
        Long numericId = resolveUuidToNumericId(userId);
        
        // Get UUIDs of everyone the current user follows
        List<Long> followingIds = followService.getFollowingIds(numericId);
        List<String> authorIds = new ArrayList<>();
        for (Long fid : followingIds) {
            userRepository.findById(fid)
                .ifPresent(u -> authorIds.add(u.getUuid()));
        }
        // Always include the current user's own posts
        authorIds.add(userId);  // userId is already UUID

        return postRepository.findByAuthorIdIn(authorIds,
            PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    /** Resolve a UUID string to the numeric User.id. */
    private Long resolveUuidToNumericId(String uuid) {
        return userRepository.findByUuid(uuid)
            .map(User::getId)
            .orElseThrow(() -> new RuntimeException("User not found: " + uuid));
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
