package com.socialcommerce.social.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    private String id;

    private String postId;
    private String authorId;
    private String authorName;
    private String text;
    private LocalDateTime createdAt = LocalDateTime.now();
}
