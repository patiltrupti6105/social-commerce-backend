package com.socialcommerce.social.document;

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
    private String content;
    private List<String> likedByUserIds = new ArrayList<>();
    private int likesCount = 0;
    
    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
    
    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    
    public List<String> getLikedByUserIds() {
        return likedByUserIds;
    }
    
    public void setLikedByUserIds(List<String> likedByUserIds) {
        this.likedByUserIds = likedByUserIds;
    }

    // ✅ NEW FIELDS (important)
    private LocalDateTime createdAt;
    private boolean isReported = false;

    // 🔹 Constructors
    public Post() {}

    public Post(String authorId, String content) {
        this.authorId = authorId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.isReported = false;
    }

    // 🔹 Getters & Setters

    public String getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isReported() {
        return isReported;
    }

    public void setReported(boolean reported) {
        isReported = reported;
    }
}