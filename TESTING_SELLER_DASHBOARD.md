# Testing Seller Dashboard Endpoint

## ✅ Endpoint Exists!

**Endpoint**: `GET /api/v1/products/seller/my`  
**Location**: `ProductController.java` line 67  
**Authentication**: Required (JWT token)

---

## 🧪 How to Test

### Method 1: Using Swagger UI (Easiest)

1. **Start Backend**:
   ```bash
   cd social-commerce-backend
   ./mvnw spring-boot:run
   ```

2. **Open Swagger**:
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. **Register/Login as Seller**:
   - Go to **Authentication** section
   - `POST /api/v1/auth/register` or login if already registered
   - Copy the `accessToken`

4. **Authorize**:
   - Click 🔓 **Authorize** button
   - Paste: `Bearer YOUR_ACCESS_TOKEN`
   - Click **Authorize** then **Close**

5. **Test Endpoint**:
   - Scroll to **Products** section
   - Find: `GET /api/v1/products/seller/my`
   - Click **Try it out**
   - Click **Execute**

6. **See Results**:
   ```json
   {
     "success": true,
     "data": [
       {
         "id": 1,
         "title": "My Product",
         "status": "DRAFT",
         "price": 25.00
       }
     ]
   }
   ```

---

### Method 2: Using cURL

```bash
# 1. Register seller
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Seller",
    "email": "seller@test.com",
    "password": "pass123",
    "role": "SELLER"
  }'

# 2. Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "seller@test.com",
    "password": "pass123"
  }'

# Response contains accessToken - copy it!

# 3. Get seller's products
curl http://localhost:8080/api/v1/products/seller/my \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

---

### Method 3: Frontend Integration

**React/TypeScript Example**:

```typescript
// api/products.ts
export const getMyProducts = async () => {
  const token = localStorage.getItem('accessToken');
  
  const response = await fetch('http://localhost:8080/api/v1/products/seller/my', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  
  if (!response.ok) {
    throw new Error('Failed to fetch products');
  }
  
  const data = await response.json();
  return data.data; // Returns array of products
};

// Usage in component
const MyProducts = () => {
  const [products, setProducts] = useState([]);
  
  useEffect(() => {
    getMyProducts()
      .then(setProducts)
      .catch(console.error);
  }, []);
  
  return (
    <div>
      <h2>My Products</h2>
      {products.map(product => (
        <div key={product.id}>
          <h3>{product.title}</h3>
          <p>Status: {product.status}</p>
          <p>Price: ${product.price}</p>
        </div>
      ))}
    </div>
  );
};
```

**Axios Example**:

```typescript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
});

// Add token to all requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const getMyProducts = () => {
  return api.get('/products/seller/my');
};
```

---

## 🔧 Troubleshooting

### Error: 401 Unauthorized

**Cause**: No token or invalid token

**Solutions**:
1. Check token is included in Authorization header
2. Token must start with `Bearer ` (note the space!)
3. Token might be expired - login again
4. Verify user is logged in

**Check token format**:
```javascript
// ✅ Correct
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

// ❌ Wrong - missing Bearer
Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

// ❌ Wrong - no space after Bearer
Authorization: BearereyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

### Error: 404 Not Found

**Possible causes**:

1. **Wrong URL**:
   ```
   ❌ http://localhost:8080/products/seller/my
   ✅ http://localhost:8080/api/v1/products/seller/my
   ```

2. **Backend not running**:
   ```bash
   # Check if running
   curl http://localhost:8080/api/v1/products
   
   # If no response, start backend
   cd social-commerce-backend
   ./mvnw spring-boot:run
   ```

3. **CORS issue** (if calling from frontend):
   ```
   // Check application.properties
   app.cors.allowed-origins=http://localhost:3000,http://localhost:5173
   ```
   Make sure your frontend URL is in the CORS whitelist!

---

### Error: Empty Array Response

**Response**:
```json
{
  "success": true,
  "data": []
}
```

**Causes**:
1. **No products created yet** - Create a product first
2. **Wrong user** - Products belong to a different seller
3. **Wrong token** - Using a different user's token

**Solution**:
```bash
# 1. Create a product first
curl -X POST http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Product",
    "description": "Test description",
    "price": 25.00,
    "categoryId": 1
  }'

# 2. Now check seller dashboard
curl http://localhost:8080/api/v1/products/seller/my \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### CORS Error in Frontend

**Error in browser console**:
```
Access to fetch at 'http://localhost:8080/api/v1/products/seller/my' 
from origin 'http://localhost:3000' has been blocked by CORS policy
```

**Solution**: Update `application.properties`:
```properties
app.cors.allowed-origins=http://localhost:3000,http://localhost:5173
```

Or check `CorsConfig.java` if it exists.

---

### Network Error / Connection Refused

**Cause**: Backend not running or wrong port

**Check**:
```bash
# Is backend running?
curl http://localhost:8080/api/v1/products

# Check what's running on port 8080
netstat -ano | findstr :8080

# Start backend if not running
cd social-commerce-backend
./mvnw spring-boot:run
```

---

## 🎯 Complete Frontend Flow

### 1. Create API Service

```typescript
// src/services/api.ts
const API_BASE_URL = 'http://localhost:8080/api/v1';

export const api = {
  // Auth
  login: async (email: string, password: string) => {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    const data = await response.json();
    if (data.success) {
      localStorage.setItem('accessToken', data.data.accessToken);
      return data.data;
    }
    throw new Error(data.message);
  },

  // Products
  getMyProducts: async () => {
    const token = localStorage.getItem('accessToken');
    const response = await fetch(`${API_BASE_URL}/products/seller/my`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    const data = await response.json();
    return data.data;
  },

  createProduct: async (product: any) => {
    const token = localStorage.getItem('accessToken');
    const response = await fetch(`${API_BASE_URL}/products`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(product)
    });
    const data = await response.json();
    return data.data;
  },

  submitProduct: async (id: number) => {
    const token = localStorage.getItem('accessToken');
    const response = await fetch(`${API_BASE_URL}/products/${id}/submit`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` }
    });
    return await response.json();
  }
};
```

### 2. Use in Component

```typescript
// src/pages/SellerDashboard.tsx
import { useEffect, useState } from 'react';
import { api } from '../services/api';

const SellerDashboard = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      const data = await api.getMyProducts();
      setProducts(data);
    } catch (err) {
      setError('Failed to load products');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (productId: number) => {
    try {
      await api.submitProduct(productId);
      loadProducts(); // Refresh list
      alert('Product submitted for review!');
    } catch (err) {
      alert('Failed to submit product');
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      <h1>My Products</h1>
      {products.length === 0 ? (
        <p>No products yet. Create your first product!</p>
      ) : (
        products.map((product: any) => (
          <div key={product.id} className="product-card">
            <h3>{product.title}</h3>
            <p>Status: <span className={`status-${product.status.toLowerCase()}`}>
              {product.status}
            </span></p>
            <p>Price: ${product.price}</p>
            
            {product.status === 'DRAFT' && (
              <button onClick={() => handleSubmit(product.id)}>
                Submit for Review
              </button>
            )}
            
            {product.status === 'PENDING_REVIEW' && (
              <p className="info">Waiting for admin approval...</p>
            )}
            
            {product.status === 'ACTIVE' && (
              <p className="success">✅ Live and visible to customers!</p>
            )}
          </div>
        ))
      )}
    </div>
  );
};
```

---

## 📋 Quick Checklist

Before calling the endpoint, verify:

- [ ] Backend is running on port 8080
- [ ] User is registered with SELLER role
- [ ] User is logged in (have access token)
- [ ] Token is included in Authorization header
- [ ] Token starts with "Bearer " (with space)
- [ ] URL is correct: `/api/v1/products/seller/my`
- [ ] CORS is configured for your frontend URL

---

## 🎯 Test Script

Save this as `test-seller-dashboard.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080/api/v1"

echo "1. Registering seller..."
curl -X POST $BASE_URL/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Seller","email":"seller@test.com","password":"pass123","role":"SELLER"}' \
  | jq

echo -e "\n2. Logging in..."
TOKEN=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"seller@test.com","password":"pass123"}' \
  | jq -r '.data.accessToken')

echo "Token: $TOKEN"

echo -e "\n3. Creating product..."
curl -X POST $BASE_URL/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Product","description":"Test","price":25.00,"categoryId":1}' \
  | jq

echo -e "\n4. Getting seller's products..."
curl $BASE_URL/products/seller/my \
  -H "Authorization: Bearer $TOKEN" \
  | jq

echo -e "\nDone! ✅"
```

Run: `bash test-seller-dashboard.sh`

---

## 📚 Related Documentation

- [PRODUCT_STATUS_GUIDE.md](./PRODUCT_STATUS_GUIDE.md) - Understanding product statuses
- [SWAGGER_GUIDE.md](./SWAGGER_GUIDE.md) - Testing with Swagger UI
- [TROUBLESHOOTING.md](./TROUBLESHOOTING.md) - Common issues

---

**The endpoint definitely exists!** If you're getting 404, double-check:
1. URL includes `/api/v1/` prefix
2. Backend is actually running
3. You're calling the correct port (8080 by default)
