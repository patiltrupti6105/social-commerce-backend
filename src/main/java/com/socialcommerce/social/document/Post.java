package com.socialcommerce.social.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import java.util.*;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "posts")
public class Post {

    @Id
    private String id;
    private int commentsCount = 0;
    private String authorId;
    private String authorName;          // denormalised for feed performance
    private String authorAvatarUrl;     // denormalised for feed performance

    @NotBlank(message = "Post content cannot be empty")
    @Size(max = 2200, message = "Post content cannot exceed 2200 characters")
    private String content;

    // Optional product tag — stores the numeric MySQL product id.
    // When set, the feed renders a "Shop this product" card below the post.
    private Long linkedProductId;

    // Denormalised product snapshot so the feed doesn't need a DB round-trip.
    private String linkedProductTitle;
    private String linkedProductImageUrl;
    private Double linkedProductPrice;

    private List<String> mediaUrls = new ArrayList<>();   // image/video URLs
    private List<String> likedByUserIds = new ArrayList<>();
    private int likesCount = 0;
    private LocalDateTime createdAt;

    // Explicit name prevents Jackson stripping "is" prefix and serializing as "reported"
    @JsonProperty("isReported")
    private boolean isReported = false;

    public Post() {}

    public Post(String authorId, String content) {
        this.authorId = authorId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.isReported = false;
    }

    // --- Getters & Setters ---

    public String getId() { return id; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorAvatarUrl() { return authorAvatarUrl; }
    public void setAuthorAvatarUrl(String authorAvatarUrl) { this.authorAvatarUrl = authorAvatarUrl; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getLinkedProductId() { return linkedProductId; }
    public void setLinkedProductId(Long linkedProductId) { this.linkedProductId = linkedProductId; }

    public String getLinkedProductTitle() { return linkedProductTitle; }
    public void setLinkedProductTitle(String linkedProductTitle) { this.linkedProductTitle = linkedProductTitle; }

    public String getLinkedProductImageUrl() { return linkedProductImageUrl; }
    public void setLinkedProductImageUrl(String linkedProductImageUrl) { this.linkedProductImageUrl = linkedProductImageUrl; }

    public Double getLinkedProductPrice() { return linkedProductPrice; }
    public void setLinkedProductPrice(Double linkedProductPrice) { this.linkedProductPrice = linkedProductPrice; }

    public List<String> getMediaUrls() { return mediaUrls; }
    public void setMediaUrls(List<String> mediaUrls) { this.mediaUrls = mediaUrls != null ? mediaUrls : new ArrayList<>(); }

    public List<String> getLikedByUserIds() { return likedByUserIds; }
    public void setLikedByUserIds(List<String> likedByUserIds) { this.likedByUserIds = likedByUserIds; }

    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }

    public int getCommentsCount() { return commentsCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isReported() { return isReported; }
    public void setReported(boolean reported) { isReported = reported; }
}
