package com.socialcommerce.social.repository;

import com.socialcommerce.social.document.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    // Feed (posts from followed users)
    Page<Post> findByAuthorIdIn(List<String> authorIds, Pageable pageable);

    // Posts of a single user
    Page<Post> findByAuthorId(String authorId, Pageable pageable);

    // Moderation (reported posts)
    List<Post> findByIsReportedTrue();
    
    List<Post> findTop10ByOrderByLikesCountDesc();
}