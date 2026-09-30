# UUID Migration Guide

## ⚠️ Do You Need This Migration?

**If your databases are EMPTY**: ✅ **Skip this entirely!** Just start the application normally. New data will automatically use UUIDs.

**If you have EXISTING data with numeric IDs**: ⚠️ **Run this migration once** to convert old data.

---

## Overview

This document explains the architectural migration from numeric IDs to UUIDs as the primary user identifier across the application.

**Note**: The migration script is only needed if you have existing MongoDB data with numeric IDs. For new projects with empty databases, the code changes alone are sufficient.

## Problem Statement

### Before Migration
The application had an architectural inconsistency:

1. **JWT Token**: Stored UUID in the `subject` claim
2. **Spring Security Principal**: Stored numeric ID as string (e.g., "2")
3. **MongoDB Documents**: Stored numeric ID strings in `authorId` fields
4. **Controllers**: Expected numeric IDs, requiring manual resolution

This caused:
- Null author names in posts/comments
- Failed comment creation (500 errors)
- Fragile resolution code scattered across controllers
- Confusion about which ID type to use

### After Migration
Consistent UUID usage everywhere:

1. **JWT Token**: UUID in subject ✓
2. **Spring Security Principal**: UUID string ✓
3. **MongoDB Documents**: UUID strings in all ID fields ✓
4. **Controllers**: Use `currentUserUuid()` from `BaseController` ✓

## Why UUIDs?

### Interview Talking Points

When discussing this migration in interviews, emphasize:

1. **Security**: UUIDs prevent user enumeration attacks
   - Can't guess `/api/users/1`, `/api/users/2` to enumerate users
   - Doesn't expose business metrics (total user count)

2. **Scalability**: UUIDs work across distributed systems
   - No need for centralized ID generation
   - Can generate IDs at any layer without coordination
   - Enables future microservice architecture

3. **Privacy**: Doesn't leak information
   - Numeric IDs reveal when accounts were created (lower = older)
   - Can't infer signup patterns or growth metrics

4. **MongoDB Best Practice**: UUIDs are standard in document databases
   - Natural fit for NoSQL where relationships are denormalized
   - Easier to reference across collections

## Architecture Changes

### 1. Authentication Filter (`JwtAuthFilter.java`)

**Before:**
```java
UsernamePasswordAuthenticationToken authentication =
    new UsernamePasswordAuthenticationToken(
        String.valueOf(user.getId()), // Numeric ID
        null,
        authorities
    );
```

**After:**
```java
UsernamePasswordAuthenticationToken authentication =
    new UsernamePasswordAuthenticationToken(
        user.getUuid(), // UUID string
        null,
        authorities
    );
```

### 2. Base Controller Pattern

Created `BaseController` with three helper methods:

```java
protected String currentUserUuid()           // Primary identifier (UUID)
protected Long currentUserNumericId()        // For MySQL foreign keys
protected User currentUser()                 // Full entity (use sparingly)
```

### 3. Controller Updates

**Social Features** (MongoDB):
- Use `currentUserUuid()` for posts, comments, likes
- Store UUID in `authorId` fields

**Commerce Features** (MySQL):
- Use `currentUserNumericId()` for orders, cart, products
- MySQL foreign keys remain numeric for database efficiency

## Migration Steps

### Step 1: Run the Migration Script

The migration script converts existing MongoDB data from numeric IDs to UUIDs.

```bash
# Add migration profile to application.properties or command line
java -jar target/backend.jar --spring.profiles.active=migration
```

**What it does:**
1. Loads all users from MySQL to build numeric ID → UUID mapping
2. Finds all MongoDB posts/comments with numeric `authorId` fields
3. Updates `authorId` to UUID
4. Updates `likedByUserIds` arrays to UUIDs
5. Logs progress and completion

**Output example:**
```
Starting MongoDB UUID migration...
Built mapping for 15 users
Found 47 posts to migrate
Migrated 47 posts
Found 128 comments to migrate
Migrated 128 comments
MongoDB UUID migration completed successfully!
IMPORTANT: Remove 'migration' profile to prevent re-running this script
```

### Step 2: Verify Migration

```javascript
// Connect to MongoDB and verify
db.posts.findOne()
// Should show: authorId: "550e8400-e29b-41d4-a716-446655440000" (UUID format)

db.comments.findOne()
// Should show: authorId: "550e8400-e29b-41d4-a716-446655440000" (UUID format)
```

### Step 3: Remove Migration Profile

After successful migration, remove the profile to prevent re-execution:

```properties
# application.properties - REMOVE THIS LINE
spring.profiles.active=migration
```

### Step 4: Deploy Updated Application

Deploy the updated backend with all controller changes. The application now uses UUIDs consistently.

## Testing Checklist

- [ ] Users can create posts with populated author names
- [ ] Users can add comments successfully
- [ ] Feed displays author information correctly
- [ ] Likes work with UUID references
- [ ] Orders/cart still work (use numeric IDs internally)
- [ ] Products can be created/updated
- [ ] Wishlist operations succeed

## Rollback Plan

If migration fails:

1. Restore MongoDB from backup (taken before migration)
2. Revert `JwtAuthFilter.java` to use `String.valueOf(user.getId())`
3. Revert all controller changes
4. Delete `BaseController.java` and `MongoUuidMigration.java`

## Files Changed

### New Files
- `BaseController.java` - Authentication helpers
- `MongoUuidMigration.java` - One-time migration script

### Modified Files
- `JwtAuthFilter.java` - Principal now stores UUID
- `PostController.java` - Uses `currentUserUuid()`
- `CommentController.java` - Uses `currentUserUuid()`
- `OrderController.java` - Uses `currentUserNumericId()`
- `CartController.java` - Uses `currentUserNumericId()`
- `ProductController.java` - Uses `currentUserNumericId()`
- `AddressController.java` - Uses `currentUserNumericId()`
- `WishlistController.java` - Uses `currentUserNumericId()`
- `ReviewController.java` - Uses `currentUserNumericId()`

## Performance Considerations

### UUID Lookups
`currentUserNumericId()` performs a database lookup:
```java
protected Long currentUserNumericId() {
    String uuid = currentUserUuid();
    return userRepository.findByUuid(uuid)
        .map(User::getId)
        .orElseThrow(() -> new RuntimeException("User not found"));
}
```

**Optimization**: Add caching if this becomes a bottleneck:
```java
@Cacheable(value = "userIdCache", key = "#uuid")
public Long getUserNumericId(String uuid) { ... }
```

### Database Indexes
Ensure UUID field is indexed in MySQL:
```sql
CREATE INDEX idx_user_uuid ON users(uuid);
```

## Future Improvements

1. **Remove numeric ID from User entity** - Make UUID the primary key
2. **Migrate MySQL foreign keys** - Use UUIDs in all tables
3. **Add UUID to API responses** - Expose only UUID, hide numeric ID
4. **Implement proper caching** - Cache UUID → numeric ID mappings

## Questions for Interviews

**Q: Why not just use numeric IDs everywhere?**
A: Numeric IDs are insecure (enumeration), leak business metrics, and don't scale to distributed systems. UUIDs are modern best practice for user-facing APIs.

**Q: Doesn't UUID lookup add latency?**
A: Yes, but it's negligible (~1ms) with proper indexing. We can add caching if needed. Security and scalability benefits outweigh minor latency.

**Q: Why keep numeric IDs in MySQL?**
A: MySQL foreign keys perform better with numeric IDs (smaller index size). It's a pragmatic compromise - UUID for external API, numeric for internal relations.

**Q: What about existing JWT tokens?**
A: Tokens already contain UUID in the subject claim. We only changed which field we read from - no token invalidation needed.

## Summary

This migration establishes UUIDs as the authoritative user identifier, improving security, scalability, and consistency. The dual-ID approach (UUID for API, numeric for MySQL FK) balances best practices with database performance.
