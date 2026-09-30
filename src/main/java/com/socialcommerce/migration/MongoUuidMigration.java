package com.socialcommerce.migration;

import com.socialcommerce.auth.entity.User;
import com.socialcommerce.auth.repository.UserRepository;
import com.socialcommerce.social.document.Comment;
import com.socialcommerce.social.document.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * One-time migration script to convert numeric IDs to UUIDs in MongoDB documents.
 * 
 * This migration addresses the architectural issue where:
 * - JWT tokens contain UUID as subject
 * - Spring Security principal was previously set to numeric ID
 * - MongoDB documents (posts, comments) stored numeric ID strings
 * 
 * After this migration:
 * - All MongoDB authorId fields contain UUIDs
 * - All likedByUserIds arrays contain UUIDs
 * - System uses UUID as the primary identifier everywhere
 * 
 * To run: Set spring.profiles.active=migration and restart the application once.
 * After successful execution, remove the profile to prevent re-running.
 */
@Component
@Profile("migration")
@RequiredArgsConstructor
@Slf4j
public class MongoUuidMigration implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        log.info("Starting MongoDB UUID migration...");
        
        try {
            // Build numeric ID → UUID mapping
            Map<String, String> numericIdToUuid = new HashMap<>();
            List<User> allUsers = userRepository.findAll();
            
            for (User user : allUsers) {
                String numericId = String.valueOf(user.getId());
                numericIdToUuid.put(numericId, user.getUuid());
            }
            
            log.info("Built mapping for {} users", numericIdToUuid.size());
            
            // Migrate Posts
            int postsUpdated = migratePosts(numericIdToUuid);
            log.info("Migrated {} posts", postsUpdated);
            
            // Migrate Comments
            int commentsUpdated = migrateComments(numericIdToUuid);
            log.info("Migrated {} comments", commentsUpdated);
            
            log.info("MongoDB UUID migration completed successfully!");
            log.info("IMPORTANT: Remove 'migration' profile to prevent re-running this script");
            
        } catch (Exception e) {
            log.error("Migration failed", e);
            throw new RuntimeException("Migration failed - check logs for details", e);
        }
    }

    private int migratePosts(Map<String, String> idMap) {
        int count = 0;
        
        // Find all posts with numeric authorId (doesn't contain hyphens, typical UUID pattern)
        Query query = new Query(Criteria.where("authorId").regex(Pattern.compile("^\\d+$")));
        List<Post> posts = mongoTemplate.find(query, Post.class);
        
        log.info("Found {} posts to migrate", posts.size());
        
        for (Post post : posts) {
            String numericId = post.getAuthorId();
            String uuid = idMap.get(numericId);
            
            if (uuid != null) {
                Update update = new Update();
                update.set("authorId", uuid);
                
                // Migrate likedByUserIds array
                if (post.getLikedByUserIds() != null && !post.getLikedByUserIds().isEmpty()) {
                    List<String> migratedLikes = post.getLikedByUserIds().stream()
                        .map(id -> idMap.getOrDefault(id, id)) // Convert numeric to UUID, keep UUID as-is
                        .toList();
                    update.set("likedByUserIds", migratedLikes);
                }
                
                mongoTemplate.updateFirst(
                    Query.query(Criteria.where("_id").is(post.getId())),
                    update,
                    Post.class
                );
                count++;
            } else {
                log.warn("No UUID mapping found for numeric ID: {}", numericId);
            }
        }
        
        return count;
    }

    private int migrateComments(Map<String, String> idMap) {
        int count = 0;
        
        // Find all comments with numeric authorId
        Query query = new Query(Criteria.where("authorId").regex(Pattern.compile("^\\d+$")));
        List<Comment> comments = mongoTemplate.find(query, Comment.class);
        
        log.info("Found {} comments to migrate", comments.size());
        
        for (Comment comment : comments) {
            String numericId = comment.getAuthorId();
            String uuid = idMap.get(numericId);
            
            if (uuid != null) {
                Update update = new Update().set("authorId", uuid);
                mongoTemplate.updateFirst(
                    Query.query(Criteria.where("_id").is(comment.getId())),
                    update,
                    Comment.class
                );
                count++;
            } else {
                log.warn("No UUID mapping found for numeric ID: {}", numericId);
            }
        }
        
        return count;
    }
}
