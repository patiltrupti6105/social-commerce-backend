package com.socialcommerce.social.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    private String id;

    private String authorId;
    private String authorName;
    private String authorAvatarUrl;
    private String caption;
    private List<String> mediaUrls = new ArrayList<>();
    private List<Long> taggedProductIds = new ArrayList<>();
    private Integer likesCount = 0;
    private Integer commentsCount = 0;
    private List<String> likedByUserIds = new ArrayList<>();
    private Boolean isReported = false;
    private List<String> reportedByUserIds = new ArrayList<>();
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
