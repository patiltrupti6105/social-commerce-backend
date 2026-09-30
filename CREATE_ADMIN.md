# How to Create an Admin User

## Method 1: Via API (Recommended for Development)

### Register with ADMIN role:
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

**Response:**
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "id": 1,
    "uuid": "550e8400-e29b-41d4-a716-446655440000",
    "email": "admin@example.com",
    "name": "Admin User",
    "role": "ADMIN"
  }
}
```

### Login as admin:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123"
  }'
```

**Save the accessToken** - you'll need it for admin operations!

---

## Method 2: Direct MySQL Insert (If API restricts ADMIN registration)

### Step 1: Generate BCrypt password hash

**Option A - Using jshell (Java 9+)**:
```bash
jshell

# In jshell:
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
var encoder = new BCryptPasswordEncoder();
System.out.println(encoder.encode("admin123"));
# Copy the output hash
```

**Option B - Using online tool**:
- Go to: https://bcrypt-generator.com/
- Enter: `admin123`
- Rounds: 10
- Copy the generated hash (starts with `$2a$10$...`)

### Step 2: Insert into MySQL
```sql
-- Connect to MySQL
mysql -u root -p

USE socialcommerce;

-- Insert admin user
INSERT INTO users (uuid, name, email, password_hash, role, is_active, created_at)
VALUES (
  '550e8400-e29b-41d4-a716-446655440000',  -- Or use UUID()
  'Admin User',
  'admin@example.com',
  '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',  -- Paste your bcrypt hash here
  'ADMIN',
  1,
  NOW()
);

-- Verify
SELECT id, uuid, name, email, role FROM users WHERE role = 'ADMIN';
```

### Step 3: Test login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123"
  }'
```

---

## Admin Capabilities

Once logged in as admin, you can:

### 1. View All Users
```bash
curl http://localhost:8080/api/v1/admin/users \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

### 2. Disable/Enable Users
```bash
# Disable user
curl -X PUT http://localhost:8080/api/v1/admin/users/{UUID}/disable \
  -H "Authorization: Bearer ADMIN_TOKEN"

# Enable user
curl -X PUT http://localhost:8080/api/v1/admin/users/{UUID}/enable \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

### 3. Grant Seller Role
```bash
curl -X PUT http://localhost:8080/api/v1/admin/users/{UUID}/grant-seller \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

### 4. Approve/Reject Products
```bash
# View pending products
curl http://localhost:8080/api/v1/admin/products/pending \
  -H "Authorization: Bearer ADMIN_TOKEN"

# Approve a product
curl -X POST http://localhost:8080/api/v1/admin/products/{PRODUCT_ID}/approve \
  -H "Authorization: Bearer ADMIN_TOKEN"

# Reject a product
curl -X POST http://localhost:8080/api/v1/admin/products/{PRODUCT_ID}/reject \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"reason": "Product images are unclear"}'
```

### 5. Moderate Reported Posts
```bash
# View reported posts
curl http://localhost:8080/api/v1/admin/posts/reported \
  -H "Authorization: Bearer ADMIN_TOKEN"

# Delete a post
curl -X DELETE http://localhost:8080/api/v1/admin/posts/{POST_ID} \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

---

## Complete Admin Workflow Example

```bash
# 1. Create admin
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Admin",
    "email": "admin@example.com",
    "password": "admin123",
    "role": "ADMIN"
  }'

# 2. Login
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@example.com", "password": "admin123"}' \
  | jq -r '.data.accessToken')

echo "Admin token: $ADMIN_TOKEN"

# 3. View pending products
curl http://localhost:8080/api/v1/admin/products/pending \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# 4. Approve product ID 1
curl -X POST http://localhost:8080/api/v1/admin/products/1/approve \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# 5. View all users
curl http://localhost:8080/api/v1/admin/users \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

---

## Testing Admin Features

### Setup test scenario:
```bash
# 1. Create a seller
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Seller",
    "email": "seller@test.com",
    "password": "pass123",
    "role": "SELLER"
  }'

# 2. Seller creates product
SELLER_TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "seller@test.com", "password": "pass123"}' \
  | jq -r '.data.accessToken')

curl -X POST http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer $SELLER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Product",
    "description": "Testing admin approval",
    "price": 29.99,
    "categoryId": 1
  }'

# 3. Seller submits for review
curl -X POST http://localhost:8080/api/v1/products/1/submit \
  -H "Authorization: Bearer $SELLER_TOKEN"

# 4. Admin approves
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@example.com", "password": "admin123"}' \
  | jq -r '.data.accessToken')

curl -X POST http://localhost:8080/api/v1/admin/products/1/approve \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# 5. Verify product is now ACTIVE
curl http://localhost:8080/api/v1/products/1
```

---

## Production Considerations

### Security Recommendations:

1. **Never use default passwords in production**
   - Change `admin123` to a strong, unique password
   - Use environment variables for sensitive data

2. **Restrict admin registration**
   - Modify `AuthService` to prevent public ADMIN registration
   - Only allow via database insertion or existing admin promotion

3. **Add role-based security**
   - Ensure `@PreAuthorize("hasRole('ADMIN')")` on admin endpoints
   - Verify JWT contains correct role claim

4. **Audit logging**
   - Log all admin actions (approve/reject/delete)
   - Track who performed each action and when

5. **Rate limiting**
   - Limit admin API calls to prevent abuse
   - Add IP-based restrictions for admin endpoints

### Example: Restrict Admin Registration

Update `AuthServiceImpl.java`:
```java
public UserDTO register(RegisterRequest request) {
    // Prevent public admin registration
    if (request.getRole() == Role.ADMIN) {
        throw new RuntimeException("Admin registration not allowed via API");
    }
    
    // ... rest of registration logic
}
```

Now admins must be created via MySQL or promoted by existing admins.

---

## Troubleshooting

### "Role not recognized"
- Ensure role is exactly: `ADMIN` (uppercase)
- Check MySQL enum definition allows `ADMIN`

### "Forbidden" when calling admin endpoints
- Verify JWT token contains `role: "ADMIN"`
- Check if security configuration allows admin role

### "Invalid password"
- BCrypt hash must be exactly 60 characters
- Ensure rounds = 10 when generating hash
- Test with a known working hash first

### Can't create first admin
- Use MySQL method (Method 2) to create initial admin
- Once first admin exists, they can promote others
