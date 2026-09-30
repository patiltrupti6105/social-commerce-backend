# Getting Started (Empty Database)

This guide is for starting fresh with empty databases. The application is already configured to use UUIDs - no migration needed!

## Prerequisites

1. **JDK 17+** installed
2. **MySQL 8.0+** running
3. **MongoDB 5.0+** running
4. **Maven 3.8+** (or use included `./mvnw`)

## Setup Steps

### 1. Configure Database Connections

Edit `src/main/resources/application.properties`:

```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/socialcommerce
spring.datasource.username=root
spring.datasource.password=your_password

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/socialcommerce

# JWT Configuration
jwt.secret=your-secret-key-minimum-256-bits-for-HS256
jwt.expiration=3600000
jwt.refresh-expiration=86400000
```

### 2. Create Databases

```sql
-- MySQL
CREATE DATABASE socialcommerce;
```

```bash
# MongoDB (auto-created on first write, but you can create it manually)
mongosh
> use socialcommerce
> db.createCollection("posts")
> db.createCollection("comments")
> db.createCollection("notifications")
```

### 3. Start the Application

```bash
# From social-commerce-backend directory
./mvnw spring-boot:run
```

The application will:
- ✅ Auto-create MySQL tables via JPA/Hibernate
- ✅ Auto-create MongoDB collections on first use
- ✅ Start on port 8080 (default)

### 4. Verify It's Running

```bash
# Health check (if you have actuator enabled)
curl http://localhost:8080/actuator/health

# Or check the console logs for:
# "Started BackendApplication in X.XXX seconds"
```

## First API Calls

### 1. Register a User

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "displayName": "John Doe",
    "role": "BUYER"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "uuid": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "displayName": "John Doe",
    "role": "BUYER"
  }
}
```

**Notice**: The user gets both:
- `id`: Numeric ID (used internally for MySQL foreign keys)
- `uuid`: UUID (used for all API operations and MongoDB documents)

### 2. Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "uuid": "550e8400-e29b-41d4-a716-446655440000",
      "email": "user@example.com",
      "displayName": "John Doe"
    }
  }
}
```

**Save the accessToken** - you'll need it for authenticated requests!

### 3. Create a Post

```bash
curl -X POST http://localhost:8080/api/v1/posts \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "My first post on the platform! 🎉"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Post created",
  "data": {
    "id": "674a5f8b9c123456789abcde",
    "authorId": "550e8400-e29b-41d4-a716-446655440000",
    "authorName": "John Doe",
    "authorAvatarUrl": null,
    "content": "My first post on the platform! 🎉",
    "likesCount": 0,
    "commentsCount": 0,
    "createdAt": "2026-09-30T15:30:00"
  }
}
```

**Notice**: The post automatically has:
- ✅ `authorId`: UUID (not numeric!)
- ✅ `authorName`: Denormalized from user table
- ✅ MongoDB document ID as `id`

### 4. Get Your Feed

```bash
curl http://localhost:8080/api/v1/posts/feed \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

All posts will have populated `authorName` and `authorAvatarUrl` - no null values!

## What's Different About This Architecture?

### UUID-First Design

Every user has **two IDs**:

1. **UUID** (`550e8400-...`): 
   - Primary identifier for API
   - Stored in JWT token
   - Used in MongoDB documents
   - Prevents user enumeration attacks

2. **Numeric ID** (`1`, `2`, `3`, ...):
   - Internal MySQL primary key
   - Used for foreign key relationships
   - Better database performance
   - Never exposed in social features

### When Each ID Is Used

```java
// Social features (Posts, Comments) → UUID
POST /api/v1/posts
{
  "authorId": "550e8400-..."  // ✅ UUID
}

// Commerce features (Orders, Cart) → Numeric ID internally
// But the API doesn't expose it - BaseController handles the conversion

// User profile → Both
GET /api/v1/users/me
{
  "id": 1,                                          // Numeric (legacy)
  "uuid": "550e8400-e29b-41d4-a716-446655440000",  // UUID (primary)
  "email": "user@example.com"
}
```

## Testing the Full Flow

### Complete User Journey

```bash
# 1. Register two users
# User A
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "alice@example.com", "password": "pass123", "displayName": "Alice", "role": "SELLER"}'

# User B  
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "bob@example.com", "password": "pass123", "displayName": "Bob", "role": "BUYER"}'

# 2. Alice logs in
ALICE_TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "alice@example.com", "password": "pass123"}' \
  | jq -r '.data.accessToken')

# 3. Alice creates a product
curl -X POST http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer $ALICE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Handmade Pottery Mug",
    "description": "Beautiful ceramic mug",
    "price": 25.00,
    "stock": 10,
    "categoryId": 1
  }'

# 4. Alice creates a post about her product
curl -X POST http://localhost:8080/api/v1/posts \
  -H "Authorization: Bearer $ALICE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content": "Check out my new pottery collection! 🏺"}'

# 5. Bob logs in
BOB_TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "bob@example.com", "password": "pass123"}' \
  | jq -r '.data.accessToken')

# 6. Bob sees Alice's post in explore feed
curl http://localhost:8080/api/v1/posts/explore \
  -H "Authorization: Bearer $BOB_TOKEN"

# 7. Bob adds a comment
curl -X POST "http://localhost:8080/api/v1/posts/{POST_ID}/comments" \
  -H "Authorization: Bearer $BOB_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content": "Looks amazing! 😍"}'

# 8. Bob likes the post
curl -X PUT "http://localhost:8080/api/v1/posts/{POST_ID}/like" \
  -H "Authorization: Bearer $BOB_TOKEN"

# 9. Bob adds product to cart
curl -X POST http://localhost:8080/api/v1/cart/items \
  -H "Authorization: Bearer $BOB_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 1}'
```

**All of these will work perfectly** because:
- Posts/comments use UUID (MongoDB)
- Cart/orders use numeric ID internally (MySQL)
- `BaseController` handles the conversion automatically

## Verifying Data in Databases

### Check MySQL

```sql
-- See users with both IDs
SELECT id, uuid, email, display_name FROM users;

-- Results:
-- id | uuid                                   | email              | display_name
-- 1  | 550e8400-e29b-41d4-a716-446655440000  | alice@example.com  | Alice
-- 2  | 7c9e6679-7425-40de-944b-e07fc1f90ae7  | bob@example.com    | Bob
```

### Check MongoDB

```javascript
// Connect to MongoDB
mongosh

use socialcommerce

// See posts with UUID authorId
db.posts.findOne()

/* Result:
{
  "_id": ObjectId("674a5f8b9c123456789abcde"),
  "authorId": "550e8400-e29b-41d4-a716-446655440000",  // ✅ UUID, not "1"
  "authorName": "Alice",
  "content": "Check out my new pottery collection! 🏺",
  "likesCount": 1,
  "likedByUserIds": [
    "7c9e6679-7425-40de-944b-e07fc1f90ae7"  // ✅ Bob's UUID
  ]
}
*/
```

**Notice**: All MongoDB IDs are UUIDs, not numeric. This happened automatically - no migration needed!

## Troubleshooting

### "User not found for UUID"

**Cause**: Token contains UUID but user doesn't exist in database.

**Fix**: The user was probably deleted. User needs to register again.

### Posts have null authorName

**Cause**: `PostService.createPost()` isn't denormalizing author info.

**Fix**: Check that `PostService` is looking up the user by UUID and setting `authorName`/`authorAvatarUrl`.

### JWT token invalid

**Cause**: `jwt.secret` changed or token expired.

**Fix**: 
- Get a new token by logging in again
- Ensure `jwt.secret` is at least 256 bits (32+ characters)

## Next Steps

- ✅ Your application is running with UUID architecture
- ✅ New data automatically uses UUIDs
- ✅ No migration script needed

**Ready to develop!** Add features, test endpoints, and enjoy the clean UUID-first architecture.

## For Interviews

When discussing this architecture:

> "We use a dual-ID pattern: UUIDs for all external APIs and MongoDB documents (security, scalability), and numeric IDs internally for MySQL foreign keys (performance). This gives us the best of both worlds - secure, distributed-system-ready identifiers externally, and efficient database joins internally. The `BaseController` pattern abstracts this complexity from developers."

See [MIGRATION_UUID.md](./MIGRATION_UUID.md) for the full architectural rationale.
