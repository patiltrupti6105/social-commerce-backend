# Troubleshooting Guide

## Common Issues & Solutions

### 1. "I created a product, but it doesn't appear!"

**Why**: Products start in `DRAFT` status and require admin approval.

**Product Lifecycle**:
```
DRAFT → Submit for Review → PENDING_REVIEW → Admin Approves → ACTIVE
```

**Solution**:

#### Step 1: Check your product status
```bash
# Login as the seller
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "seller@example.com", "password": "password123"}'

# Get your products (using the token)
curl http://localhost:8080/api/v1/products/seller/my \
  -H "Authorization: Bearer YOUR_TOKEN"
```

You'll see:
```json
{
  "success": true,
  "data": [{
    "id": 1,
    "title": "My Product",
    "status": "DRAFT",  // ← Still in draft!
    ...
  }]
}
```

#### Step 2: Submit for review
```bash
curl -X POST http://localhost:8080/api/v1/products/1/submit \
  -H "Authorization: Bearer SELLER_TOKEN"
```

Now status is `PENDING_REVIEW`.

#### Step 3: Admin approves it
```bash
# Login as admin (see "How to create admin" below)
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@example.com", "password": "admin123"}'

# Approve the product
curl -X POST http://localhost:8080/api/v1/admin/products/1/approve \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

#### Step 4: Now it appears!
```bash
# Public product search (no auth needed)
curl http://localhost:8080/api/v1/products

# Should return the approved product with status: "ACTIVE"
```

---

### 2. "How do I create an admin user?"

**Option A: Register with ADMIN role (if allowed)**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Admin User",
    "email": "admin@example.com",
    "password": "admin123",
    "role": "ADMIN"
  }'
```

**Option B: Directly in MySQL (if registration restricts ADMIN role)**
```sql
-- Connect to MySQL
mysql -u root -p

USE socialcommerce;

-- Create admin user
INSERT INTO users (uuid, name, email, password_hash, role, is_active, created_at)
VALUES (
  UUID(),
  'Admin User',
  'admin@example.com',
  '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',  -- Use bcrypt hash
  'ADMIN',
  1,
  NOW()
);

-- To generate a bcrypt hash, use this Java code or online tool:
-- BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
-- String hash = encoder.encode("admin123");
```

**Option C: Quick bcrypt hash generator**
```java
// Run this in jshell or create a simple Java file
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
System.out.println(encoder.encode("admin123"));
```

**Verify admin was created**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@example.com", "password": "admin123"}'

# Should return token with role: "ADMIN"
```

---

### 3. "Users can't create posts!"

**Symptoms**:
- POST /api/v1/posts returns 500 error
- Posts created with null author name
- "User not found" errors in logs

**Root Causes & Solutions**:

#### Issue A: PostService expects numeric ID but gets UUID

**Check logs**:
```
java.lang.NumberFormatException: For input string: "550e8400-e29b-41d4-a716-446655440000"
```

**Solution**: The `PostService.getFeed()` has old code. Update it:

```java
// OLD CODE (broken):
public Page<Post> getFeed(String userId, int page, int size) {
    Long numericId = resolveNumericId(userId);  // Tries to parse UUID as number
    ...
}

// FIXED CODE:
public Page<Post> getFeed(String userId, int page, int size) {
    // userId is now UUID from JwtAuthFilter
    List<Long> followingIds = followService.getFollowingIds(
        resolveUuidToNumericId(userId)  // Convert UUID → numeric for MySQL query
    );
    ...
}
```

#### Issue B: User.getName() returns null

**Check in MySQL**:
```sql
SELECT id, uuid, name, email FROM users WHERE uuid = 'YOUR_UUID';
```

If `name` is NULL, update it:
```sql
UPDATE users SET name = 'John Doe' WHERE uuid = 'YOUR_UUID';
```

Or ensure registration includes name:
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",  # ← Make sure this is included
    "email": "john@example.com",
    "password": "pass123",
    "role": "BUYER"
  }'
```

#### Issue C: PostService still uses old ID resolution

**Fix the PostService.createPost()**:

The service should already be updated after our UUID migration. Verify:
```java
public Post createPost(Post post) {
    // post.getAuthorId() should already be UUID from PostController
    userRepository.findByUuid(post.getAuthorId()).ifPresent(user -> {
        post.setAuthorName(user.getName());  // ✅ Should work
        ...
    });
    return postRepository.save(post);
}
```

If it's not updated, the issue is PostService wasn't recompiled. Run:
```bash
./mvnw clean compile
./mvnw spring-boot:run
```

---

### 4. "Feed returns empty or posts have null authors"

**Symptoms**:
```json
{
  "id": "674a...",
  "authorId": "550e8400-...",
  "authorName": null,  // ← Problem
  "content": "My post"
}
```

**Root Cause**: Author denormalization failed during post creation.

**Solutions**:

#### Quick Fix: Re-denormalize existing posts
```javascript
// Connect to MongoDB
mongosh

use socialcommerce

// Find posts with null authorName
db.posts.find({ authorName: null })

// For each post, manually update (or create a script)
db.posts.updateOne(
  { _id: ObjectId("674a...") },
  { $set: { 
    authorName: "John Doe",
    authorAvatarUrl: "https://example.com/avatar.jpg"
  }}
)
```

#### Permanent Fix: Ensure PostService denormalizes correctly

Check `PostService.createPost()`:
```java
userRepository.findByUuid(post.getAuthorId()).ifPresent(user -> {
    post.setAuthorName(user.getName());  // Make sure user.getName() is not null
    userProfileRepository.findById(user.getId()).ifPresent(profile ->
        post.setAuthorAvatarUrl(profile.getAvatarUrl()));
});
```

**Test it**:
```bash
# Create a post
curl -X POST http://localhost:8080/api/v1/posts \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content": "Test post"}'

# Response should have populated authorName:
{
  "authorId": "550e8400-...",
  "authorName": "John Doe",  // ✅ Not null!
  "content": "Test post"
}
```

---

### 5. "JWT token errors"

**"Invalid JWT signature"**:
- Cause: `jwt.secret` changed after token was issued
- Solution: Login again to get a new token

**"JWT expired"**:
- Cause: Token lifetime exceeded (default 1 hour)
- Solution: Use refresh token endpoint:
```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "YOUR_REFRESH_TOKEN"}'
```

**"User not found for UUID"**:
- Cause: User in JWT doesn't exist in database
- Solution: Re-register or restore user record

---

### 6. "MongoDB connection refused"

**Error**: `MongoSocketOpenException: Exception opening socket`

**Solutions**:
```bash
# Check if MongoDB is running
mongosh

# If not running, start it:
# Windows:
net start MongoDB

# Mac:
brew services start mongodb-community

# Linux:
sudo systemctl start mongod

# Update application.properties:
spring.data.mongodb.uri=mongodb://localhost:27017/socialcommerce
```

---

### 7. "MySQL connection refused"

**Error**: `CommunicationsException: Communications link failure`

**Solutions**:
```bash
# Check if MySQL is running
mysql -u root -p

# If not running, start it:
# Windows:
net start MySQL80

# Mac:
brew services start mysql

# Linux:
sudo systemctl start mysql

# Verify credentials in application.properties:
spring.datasource.url=jdbc:mysql://localhost:3306/socialcommerce
spring.datasource.username=root
spring.datasource.password=your_actual_password
```

---

## Quick Diagnostic Commands

### Check User Data
```sql
-- MySQL
SELECT id, uuid, name, email, role FROM users;
```

### Check Product Status
```sql
-- MySQL
SELECT id, title, status, seller_id FROM products;
```

### Check Posts
```javascript
// MongoDB
db.posts.find().pretty()

// Check for UUID vs numeric ID
db.posts.aggregate([
  {
    $project: {
      authorId: 1,
      isUuid: { $regexMatch: { input: "$authorId", regex: /^[0-9a-f-]{36}$/i }}
    }
  }
])
```

### Check Comments
```javascript
// MongoDB
db.comments.find().pretty()
```

---

## Product Approval Workflow (Complete Example)

```bash
# 1. SELLER: Register
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Seller Sam",
    "email": "seller@example.com",
    "password": "pass123",
    "role": "SELLER"
  }'

# 2. SELLER: Login
SELLER_TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "seller@example.com", "password": "pass123"}' \
  | jq -r '.data.accessToken')

# 3. SELLER: Create product (status = DRAFT)
curl -X POST http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer $SELLER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Handmade Mug",
    "description": "Beautiful ceramic mug",
    "price": 25.00,
    "categoryId": 1
  }'
# Returns: { "id": 1, "status": "DRAFT", ... }

# 4. SELLER: Submit for review
curl -X POST http://localhost:8080/api/v1/products/1/submit \
  -H "Authorization: Bearer $SELLER_TOKEN"
# Status changes to: PENDING_REVIEW

# 5. ADMIN: Login
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@example.com", "password": "admin123"}' \
  | jq -r '.data.accessToken')

# 6. ADMIN: View pending products
curl http://localhost:8080/api/v1/admin/products/pending \
  -H "Authorization: Bearer $ADMIN_TOKEN"
# Shows: [{ "id": 1, "title": "Handmade Mug", "status": "PENDING_REVIEW" }]

# 7. ADMIN: Approve product
curl -X POST http://localhost:8080/api/v1/admin/products/1/approve \
  -H "Authorization: Bearer $ADMIN_TOKEN"
# Status changes to: ACTIVE

# 8. PUBLIC: Now appears in search
curl http://localhost:8080/api/v1/products
# Shows: [{ "id": 1, "title": "Handmade Mug", "status": "ACTIVE" }]
```

---

## Still Having Issues?

1. **Check application logs** for stack traces
2. **Verify database connections** (MySQL & MongoDB both running)
3. **Rebuild project**: `./mvnw clean compile`
4. **Check JWT token** is valid and not expired
5. **Verify user exists** in MySQL with correct UUID
6. **Check MongoDB documents** use UUID format (not numeric)

If posts still fail, the issue is likely in `PostService.createPost()` - ensure it's using the updated UUID-based code from our migration.
