# Product Status Guide - Where Are My Products?

## 🤔 The Question: "I created a product, where did it go?"

**Answer**: Your product is saved! It's just in **DRAFT** status and hidden from public view.

---

## 📊 Product Status Flow

```
┌─────────┐    Submit     ┌──────────────────┐    Admin        ┌────────┐
│  DRAFT  │─────────────▶│ PENDING_REVIEW   │───Approve──────▶│ ACTIVE │
└─────────┘               └──────────────────┘                 └────────┘
   ↑                             │                                  │
   │                             │ Admin                            │
   │                             │ Reject                           │
   │                             ▼                                  │
   │                      ┌──────────┐                             │
   └──────────────────────│ REJECTED │                             │
                          └──────────┘                             │
                                                                    │
                                                            Seller Archives
                                                                    ▼
                                                            ┌──────────┐
                                                            │ ARCHIVED │
                                                            └──────────┘
```

---

## 🔍 Where to Find Products by Status

### 1. **Your Draft Products (Seller Dashboard)**

**Endpoint**: `GET /api/v1/products/seller/my`

**This shows ALL your products** regardless of status.

**Swagger Steps**:
1. Open: http://localhost:8080/swagger-ui.html
2. Authorize with your seller token
3. Find: **Products** → `GET /api/v1/products/seller/my`
4. Click "Try it out" → "Execute"

**cURL**:
```bash
curl http://localhost:8080/api/v1/products/seller/my \
  -H "Authorization: Bearer YOUR_SELLER_TOKEN"
```

**Response**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "Handmade Pottery Mug",
      "status": "DRAFT",           // ← Your draft!
      "price": 25.00,
      "sellerId": 2,
      "createdAt": "2026-09-30T15:00:00"
    },
    {
      "id": 2,
      "title": "Ceramic Bowl",
      "status": "PENDING_REVIEW",  // ← Waiting for admin
      "price": 35.00,
      "sellerId": 2,
      "createdAt": "2026-09-30T16:00:00"
    },
    {
      "id": 3,
      "title": "Tea Set",
      "status": "ACTIVE",          // ← Public can see this
      "price": 120.00,
      "sellerId": 2,
      "createdAt": "2026-09-30T14:00:00"
    }
  ]
}
```

---

### 2. **Public Product Search** (Only ACTIVE)

**Endpoint**: `GET /api/v1/products`

**Important**: This endpoint **ONLY shows ACTIVE products**. Drafts are intentionally hidden.

**Why?** The repository query filters by status:
```java
@Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND ...")
Page<Product> searchProducts(...);
```

**Public Search Shows**:
- ✅ ACTIVE products
- ❌ DRAFT products (hidden)
- ❌ PENDING_REVIEW products (hidden)
- ❌ REJECTED products (hidden)
- ❌ ARCHIVED products (hidden)

---

### 3. **Admin Pending Products**

**Endpoint**: `GET /api/v1/admin/products/pending`

**Shows**: Products with status = `PENDING_REVIEW`

**Swagger Steps**:
1. Authorize with admin token
2. Find: **Admin** → `GET /api/v1/admin/products/pending`
3. Click "Try it out" → "Execute"

**cURL**:
```bash
curl http://localhost:8080/api/v1/admin/products/pending \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

---

### 4. **Direct Database Query**

**MySQL**:
```sql
-- All products
SELECT id, title, status, seller_id, created_at 
FROM products 
ORDER BY created_at DESC;

-- Only drafts
SELECT id, title, status, seller_id 
FROM products 
WHERE status = 'DRAFT';

-- Products by specific seller
SELECT id, title, status 
FROM products 
WHERE seller_id = 2;  -- Replace with your user ID
```

---

## 🎯 Complete Workflow (Start to Finish)

### Step 1: Create Product (DRAFT)
```bash
POST /api/v1/products
{
  "title": "Handmade Mug",
  "description": "Beautiful ceramic mug",
  "price": 25.00,
  "categoryId": 1
}
```

**Status**: `DRAFT`  
**Visible in**:
- ✅ Seller dashboard (`/products/seller/my`)
- ❌ Public search (`/products`)
- ❌ Admin pending (`/admin/products/pending`)

---

### Step 2: Submit for Review
```bash
POST /api/v1/products/1/submit
```

**Status**: `PENDING_REVIEW`  
**Visible in**:
- ✅ Seller dashboard
- ✅ Admin pending queue
- ❌ Public search

**What happens**: Product appears in admin's pending queue for review.

---

### Step 3: Admin Approves
```bash
POST /api/v1/admin/products/1/approve
```

**Status**: `ACTIVE`  
**Visible in**:
- ✅ Seller dashboard
- ✅ Public search **← NOW APPEARS!**
- ❌ Admin pending (already approved)

**What happens**: Product is now publicly visible and purchasable.

---

## 🔧 Common Scenarios

### Scenario 1: "I just created a product, where is it?"

**Check**:
```bash
curl http://localhost:8080/api/v1/products/seller/my \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Expected**: You'll see it with `status: "DRAFT"`

**To make it public**:
1. Submit for review: `POST /products/{id}/submit`
2. Get admin to approve: `POST /admin/products/{id}/approve`

---

### Scenario 2: "I submitted my product, why isn't it showing?"

**Status**: Probably `PENDING_REVIEW`

**Check admin queue**:
```bash
curl http://localhost:8080/api/v1/admin/products/pending \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

**Solution**: Wait for admin to approve, or approve it yourself if you're admin.

---

### Scenario 3: "Admin rejected my product, where did it go?"

**Status**: `REJECTED`

**Check your dashboard**:
```bash
curl http://localhost:8080/api/v1/products/seller/my \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**You'll see**:
```json
{
  "id": 1,
  "status": "REJECTED",
  "rejectionReason": "Product images are unclear"
}
```

**What to do**:
1. Fix the issues mentioned in `rejectionReason`
2. Update product: `PUT /products/{id}`
3. Submit again: `POST /products/{id}/submit`

---

## 📋 Quick Reference

| Status | Seller Sees | Public Sees | Admin Sees | Can Edit | Next Action |
|--------|------------|-------------|------------|----------|-------------|
| **DRAFT** | ✅ Yes | ❌ No | ❌ No | ✅ Yes | Submit for review |
| **PENDING_REVIEW** | ✅ Yes | ❌ No | ✅ Yes | ❌ No | Wait for admin |
| **ACTIVE** | ✅ Yes | ✅ Yes | ✅ Yes | ⚠️ Limited | Can archive |
| **REJECTED** | ✅ Yes | ❌ No | ✅ Yes | ✅ Yes | Fix & resubmit |
| **ARCHIVED** | ✅ Yes | ❌ No | ✅ Yes | ❌ No | - |

---

## 🎨 Using Swagger UI

### Find Your Drafts (Visual Guide)

1. **Open Swagger**: http://localhost:8080/swagger-ui.html

2. **Authorize**: 
   - Click 🔓 button
   - Paste: `Bearer YOUR_SELLER_TOKEN`

3. **Navigate to Products Section**

4. **Click**: `GET /api/v1/products/seller/my`

5. **Try It Out** → **Execute**

6. **See Results**: All your products with their status

### Submit Draft for Review

1. **Find Product ID** from seller dashboard

2. **Click**: `POST /api/v1/products/{id}/submit`

3. **Enter ID** in the `id` parameter field

4. **Execute**

5. **Verify**: Status changes to `PENDING_REVIEW`

### Approve Product (Admin)

1. **Re-authorize** with admin token

2. **View Pending**: `GET /api/v1/admin/products/pending`

3. **Approve**: `POST /api/v1/admin/products/{id}/approve`

4. **Verify**: Check public products now shows it

---

## 💡 Tips

### Tip 1: Use Seller Dashboard as Home
Bookmark the seller dashboard endpoint in Swagger. This is your "product management home" where you see everything.

### Tip 2: Status is Your Friend
Always check the `status` field to understand where your product is in the workflow.

### Tip 3: Don't Panic if Product Disappears
It's still in the database! Just check the seller dashboard - it's always there regardless of status.

### Tip 4: Set Up Admin Account Early
You'll need admin approval frequently during testing. Have an admin account ready.

### Tip 5: Test the Full Flow
Create → Submit → Approve → Verify public visibility. This confirms everything works.

---

## 🐛 Troubleshooting

### "My dashboard shows empty array"

**Possible causes**:
1. You're not logged in as the seller who created the products
2. Products belong to a different user ID
3. No products exist yet

**Solution**:
```sql
-- Check which user created which products
SELECT id, title, seller_id FROM products;

-- Check your current user ID
SELECT id, uuid, email FROM users WHERE email = 'your@email.com';
```

### "Can't submit product for review"

**Error**: "Can only submit DRAFT or REJECTED products"

**Solution**: Product must be in DRAFT or REJECTED status. Check current status:
```bash
GET /api/v1/products/seller/my
```

### "Public search never shows my product"

**Checklist**:
- [ ] Product status is `ACTIVE` (not DRAFT or PENDING)
- [ ] Product is not archived
- [ ] You're checking the right endpoint (`GET /products`, not `/seller/my`)
- [ ] Search filters aren't excluding it

---

## 🎯 Summary

| I Want To... | Use This Endpoint |
|-------------|-------------------|
| See all my products | `GET /products/seller/my` |
| See public products | `GET /products` |
| Submit draft | `POST /products/{id}/submit` |
| Update draft | `PUT /products/{id}` |
| See pending (admin) | `GET /admin/products/pending` |
| Approve (admin) | `POST /admin/products/{id}/approve` |
| Reject (admin) | `POST /admin/products/{id}/reject` |

**Key Takeaway**: Your drafts are safe! They're just hidden from public view until approved. Use the seller dashboard to manage them.

---

**Need more help?** See [TROUBLESHOOTING.md](./TROUBLESHOOTING.md) for detailed debugging steps.
