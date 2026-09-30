# Swagger UI Guide

## 🚀 Quick Start

### 1. Start the Application
```bash
cd social-commerce-backend
./mvnw spring-boot:run
```

### 2. Access Swagger UI
Open your browser and go to:
```
http://localhost:8080/swagger-ui.html
```

Or access the raw OpenAPI JSON:
```
http://localhost:8080/v3/api-docs
```

---

## 🔐 How to Test Protected Endpoints

Most endpoints require authentication. Here's how to set it up in Swagger:

### Step 1: Register a User
1. Find **Authentication** section in Swagger UI
2. Click on `POST /api/v1/auth/register`
3. Click **"Try it out"**
4. Edit the request body:
```json
{
  "name": "Test User",
  "email": "test@example.com",
  "password": "password123",
  "role": "BUYER"
}
```
5. Click **"Execute"**
6. You should get a `201 Created` response

### Step 2: Login to Get Token
1. Click on `POST /api/v1/auth/login`
2. Click **"Try it out"**
3. Enter credentials:
```json
{
  "email": "test@example.com",
  "password": "password123"
}
```
4. Click **"Execute"**
5. **Copy the `accessToken`** from the response:
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  // ← Copy this!
    "refreshToken": "...",
    "user": { ... }
  }
}
```

### Step 3: Authorize Swagger
1. Click the 🔓 **"Authorize"** button at the top right
2. In the modal, paste your token:
   ```
   Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```
   **Important**: Include the word `Bearer` followed by a space!
3. Click **"Authorize"**
4. Click **"Close"**

✅ **Now all protected endpoints will work!** The padlock icons 🔒 will turn closed.

---

## 📋 Testing Complete Workflows

### Workflow 1: Product Lifecycle

#### 1. Create Seller Account
```json
POST /api/v1/auth/register
{
  "name": "Seller Sam",
  "email": "seller@example.com",
  "password": "pass123",
  "role": "SELLER"
}
```

#### 2. Login as Seller
```json
POST /api/v1/auth/login
{
  "email": "seller@example.com",
  "password": "pass123"
}
```
→ Copy token and authorize

#### 3. Create Product
```json
POST /api/v1/products
{
  "title": "Handmade Pottery Mug",
  "description": "Beautiful ceramic mug, handcrafted",
  "price": 25.00,
  "categoryId": 1
}
```
→ Note the product ID in response

#### 4. Submit for Review
```
POST /api/v1/products/{id}/submit
```
→ Status changes to `PENDING_REVIEW`

#### 5. Login as Admin
```json
POST /api/v1/auth/login
{
  "email": "admin@example.com",
  "password": "admin123"
}
```
→ Re-authorize with admin token

#### 6. Approve Product
```
POST /api/v1/admin/products/{id}/approve
```
→ Status changes to `ACTIVE`

#### 7. View Public Products
```
GET /api/v1/products
```
→ Product now appears in list (no auth needed!)

---

### Workflow 2: Social Features

#### 1. Create Post
```json
POST /api/v1/posts
{
  "content": "Check out my new pottery collection! 🏺",
  "linkedProductId": 1
}
```

#### 2. Get Feed
```
GET /api/v1/posts/feed
```
→ See posts from followed users

#### 3. Like a Post
```
PUT /api/v1/posts/{postId}/like
```

#### 4. Add Comment
```json
POST /api/v1/posts/{postId}/comments
{
  "content": "Beautiful work! 😍"
}
```

#### 5. Explore Posts
```
GET /api/v1/posts/explore
```
→ See top posts (no auth needed!)

---

### Workflow 3: Shopping

#### 1. Browse Products
```
GET /api/v1/products?page=0&size=20
```

#### 2. Search Products
```
GET /api/v1/products?q=mug&minPrice=10&maxPrice=50
```

#### 3. Add to Cart
```json
POST /api/v1/cart/items
{
  "productId": 1,
  "quantity": 2
}
```

#### 4. View Cart
```
GET /api/v1/cart
```

#### 5. Checkout
```json
POST /api/v1/orders/checkout
{
  "addressId": 1,
  "paymentMethod": "CREDIT_CARD"
}
```

#### 6. View Orders
```
GET /api/v1/orders
```

---

## 🎯 API Sections

Swagger organizes endpoints into these sections:

### 🔐 Authentication
- Register, login, refresh token, logout
- Password reset

### 📦 Products
- Browse, search, create products
- Seller management
- Submit for review

### 👑 Admin
- Approve/reject products
- Manage users
- Moderate content

### 💬 Social
- Posts, comments, likes
- Feed and explore

### 🛒 Shopping
- Cart management
- Checkout
- Order history

### ⭐ Reviews
- Product reviews and ratings

### 🔔 Notifications
- User notifications

---

## 🔧 Tips & Tricks

### Use Filters
Swagger has a search box at the top - use it to find endpoints quickly:
- Type "post" to see all post-related endpoints
- Type "admin" to see admin endpoints

### Try Different Roles
Test with different user roles to see permissions:
```json
// Buyer
{"role": "BUYER"}

// Seller (can create products)
{"role": "SELLER"}

// Admin (can approve products, manage users)
{"role": "ADMIN"}
```

### Check Response Schemas
Click on any response code (200, 201, etc.) to see the expected response structure.

### Copy as cURL
Swagger can generate cURL commands:
1. Execute any request
2. Look for the cURL command in the response section
3. Copy and run in terminal

### Test Error Cases
Try invalid inputs to see error responses:
- Empty required fields
- Invalid email format
- Expired tokens
- Unauthorized access

---

## 🐛 Troubleshooting

### "401 Unauthorized" on Protected Endpoints
**Solution**: 
1. Check if you clicked **Authorize** button
2. Ensure token includes `Bearer ` prefix
3. Token might be expired - login again to get a new one

### "403 Forbidden"
**Solution**: Your role doesn't have permission for this endpoint
- Admin endpoints need ADMIN role
- Seller endpoints need SELLER or ADMIN role

### Can't See New Product
**Solution**: Products need admin approval
1. Submit product: `POST /products/{id}/submit`
2. Login as admin
3. Approve: `POST /admin/products/{id}/approve`

### Swagger UI Not Loading
**Solution**:
1. Ensure application is running: `./mvnw spring-boot:run`
2. Check correct URL: `http://localhost:8080/swagger-ui.html`
3. Clear browser cache if needed

### "Failed to fetch" Errors
**Solution**:
1. Check if backend is running
2. Verify port 8080 is not blocked by firewall
3. Try `http://localhost:8080/v3/api-docs` to test API docs endpoint

---

## 📊 Advanced Features

### Filtering Results
Many list endpoints support filters:
```
GET /api/v1/products?q=pottery&categoryId=1&minPrice=10&maxPrice=100&sortBy=price_asc
```

### Pagination
List endpoints support pagination:
```
GET /api/v1/posts/feed?page=0&size=20
```

### Sorting
Some endpoints allow sorting:
```
GET /api/v1/products?sortBy=price_asc
GET /api/v1/products?sortBy=price_desc
GET /api/v1/products?sortBy=rating
```

---

## 💡 Example Scenarios

### Scenario 1: New Seller Onboarding
1. Register as SELLER
2. Login and authorize
3. Create first product
4. Submit for review
5. Wait for admin approval
6. Create post about product
7. Product appears in public listings

### Scenario 2: Buyer Journey
1. Register as BUYER
2. Browse products (no auth needed)
3. Login and authorize
4. Add items to cart
5. Create shipping address
6. Checkout
7. View order history

### Scenario 3: Admin Moderation
1. Login as ADMIN
2. View pending products: `GET /admin/products/pending`
3. Approve or reject each
4. View reported posts: `GET /admin/posts/reported`
5. Delete inappropriate content

---

## 🌟 Best Practices

### For Testing
1. Start with authentication endpoints
2. Keep tokens in a text file for reuse
3. Test happy path first, then error cases
4. Use realistic test data
5. Clean up test data after testing

### For Development
1. Add `@Tag` annotations to group endpoints
2. Use `@Operation` for clear descriptions
3. Document all possible response codes
4. Include example request/response bodies
5. Keep descriptions concise but informative

---

## 📚 Additional Resources

- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **Main README**: [README.md](./README.md)
- **Troubleshooting**: [TROUBLESHOOTING.md](./TROUBLESHOOTING.md)
- **Getting Started**: [GETTING_STARTED.md](./GETTING_STARTED.md)

---

## 🎓 For Interviews

When discussing Swagger in interviews:

> "I integrated SpringDoc OpenAPI to provide interactive API documentation. This allows:
> - **Self-service testing** for frontend developers
> - **Clear API contracts** with request/response schemas
> - **Security documentation** showing JWT bearer authentication
> - **Reduced onboarding time** - new developers can explore the API visually
> 
> The Swagger UI is configured to work with our JWT authentication through the bearer auth scheme, and all endpoints are properly documented with tags, descriptions, and response codes."

**Key points to mention**:
- SpringDoc OpenAPI vs older Swagger libraries (modern choice)
- Integration with Spring Security
- API-first development approach
- Living documentation that stays in sync with code
