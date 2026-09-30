# Social Commerce Backend

A modern social commerce platform built with Spring Boot, featuring hybrid MySQL + MongoDB architecture and UUID-based authentication.

## Architecture Highlights

### Authentication & Identity
- **UUID-first design**: All users identified by UUID externally
- **Dual-ID pattern**: UUID for API/MongoDB, numeric for MySQL foreign keys
- **JWT-based auth**: Secure token authentication with UUID as subject
- **BaseController pattern**: Centralized authentication helpers

### Database Architecture
- **MySQL**: Users, products, orders, cart (relational data with foreign keys)
- **MongoDB**: Posts, comments, notifications (social features with denormalization)

## Recent Architecture Migration

We completed a comprehensive migration from mixed numeric/UUID identifiers to a consistent UUID-first architecture. This addressed:
- ✅ User enumeration vulnerabilities
- ✅ Inconsistent ID types across layers
- ✅ Failed author denormalization in MongoDB
- ✅ Scattered ID resolution code

**See full details**: [MIGRATION_UUID.md](./MIGRATION_UUID.md)

## Quick Start

**📖 Documentation Hub:**
- 🚀 [**GETTING_STARTED.md**](./GETTING_STARTED.md) - New project setup (empty database)
- 📘 [**SWAGGER_GUIDE.md**](./SWAGGER_GUIDE.md) - **Interactive API testing with Swagger UI**
- 📦 [**PRODUCT_STATUS_GUIDE.md**](./PRODUCT_STATUS_GUIDE.md) - **Where are my draft products?**
- 🔧 [**TROUBLESHOOTING.md**](./TROUBLESHOOTING.md) - Fix common issues
- 👑 [**CREATE_ADMIN.md**](./CREATE_ADMIN.md) - Create admin user
- 💬 **Posts failing?** → Already fixed in UUID migration
- 🔄 [**HOW_TO_MIGRATE.md**](./HOW_TO_MIGRATE.md) - Only for existing data

**🎯 Quick API Testing:**
1. Start application: `./mvnw spring-boot:run`
2. Open Swagger UI: **http://localhost:8080/swagger-ui.html**
3. Test all endpoints interactively!

**Quick Reference:** See inline guide below ⬇️

### Prerequisites
- JDK 17+
- MySQL 8.0+
- MongoDB 5.0+
- Maven 3.8+

### Run Application
```bash
# Install dependencies and compile
./mvnw clean install

# Run application
./mvnw spring-boot:run
```

### Run UUID Migration (First Time Only)
**⚠️ SKIP THIS if your database is empty!** Only needed for existing data.

If you have existing data with numeric IDs:
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=migration"
```
**See**: [HOW_TO_MIGRATE.md](./HOW_TO_MIGRATE.md)

Otherwise, just run normally - new data will use UUIDs automatically.

## Project Structure

```
src/main/java/com/socialcommerce/
├── auth/              # Authentication & user management (MySQL)
├── catalog/           # Product catalog & categories (MySQL)
├── orders/            # Orders, cart, addresses (MySQL)
├── social/            # Posts, comments, likes (MongoDB)
├── notifications/     # User notifications (MongoDB)
├── reviews/           # Product reviews (MySQL)
├── wishlist/          # User wishlists (MySQL)
├── admin/             # Admin panel features
├── analytics/         # Analytics & reporting
├── config/            # Security, JWT, database config
├── common/            # Shared DTOs, responses, BaseController
└── migration/         # One-time data migration scripts
```

## Key Design Patterns

### BaseController Pattern
All controllers extend `BaseController` for consistent authentication:
```java
@RestController
public class PostController extends BaseController {
    @PostMapping
    public Post createPost(@RequestBody Post post) {
        post.setAuthorId(currentUserUuid());  // UUID for MongoDB
        return postService.create(post);
    }
}

@RestController  
public class OrderController extends BaseController {
    @PostMapping
    public Order checkout() {
        return orderService.checkout(currentUserNumericId());  // Numeric for MySQL FK
    }
}
```

### Response Wrapping
All API responses use consistent structure:
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

## API Documentation

### Swagger UI (Recommended)
Interactive API documentation with testing capabilities:
```
http://localhost:8080/swagger-ui.html
```

Features:
- ✅ Test all endpoints directly in browser
- ✅ See request/response schemas
- ✅ JWT authentication support
- ✅ No need for Postman or cURL
- ✅ See [SWAGGER_GUIDE.md](./SWAGGER_GUIDE.md) for detailed instructions

### Raw OpenAPI JSON
```
http://localhost:8080/v3/api-docs
```

### Manual Testing (Alternative)

If you prefer cURL or Postman, here are the main endpoints:

### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login (returns JWT)
- `POST /api/v1/auth/refresh` - Refresh access token

### Social Features (MongoDB)
- `POST /api/v1/posts` - Create post
- `GET /api/v1/posts/feed` - Get personalized feed
- `POST /api/v1/posts/{id}/comments` - Add comment
- `PUT /api/v1/posts/{id}/like` - Toggle like

### Commerce Features (MySQL)
- `GET /api/v1/products` - Browse products
- `POST /api/v1/cart/items` - Add to cart
- `POST /api/v1/orders/checkout` - Place order
- `GET /api/v1/orders` - Order history

## Configuration

### application.properties
```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/socialcommerce
spring.datasource.username=root
spring.datasource.password=password

# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/socialcommerce

# JWT
jwt.secret=your-secret-key-min-256-bits
jwt.expiration=3600000
jwt.refresh-expiration=86400000
```

## Testing

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=PostServiceTest
```

## Interview Talking Points

This project demonstrates:

1. **System Design**
   - Hybrid database architecture (SQL + NoSQL)
   - UUID-first design for security and scalability
   - Denormalization strategies in MongoDB

2. **Security**
   - JWT authentication with refresh tokens
   - UUID to prevent user enumeration
   - Role-based access control (BUYER/SELLER/ADMIN)

3. **Code Quality**
   - BaseController pattern for DRY authentication
   - Consistent API response wrapping
   - Comprehensive validation and error handling

4. **DevOps**
   - Safe data migration scripts with rollback plans
   - Spring profiles for environment-specific configs
   - Docker Compose for local development

5. **Architecture Evolution**
   - Identified and fixed fundamental UUID/numeric ID inconsistency
   - Migrated existing data systematically
   - Balanced idealism (UUID everywhere) with pragmatism (MySQL numeric FKs)

## Contributing

When adding new features:
1. Controllers should extend `BaseController`
2. Use `currentUserUuid()` for MongoDB features
3. Use `currentUserNumericId()` for MySQL foreign keys
4. Wrap responses in `ApiResponse<T>`
5. Add comprehensive validation

## License

MIT License - see LICENSE file for details
