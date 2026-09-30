# Quick Migration Guide

## ⚠️ Do You Need This Migration?

**If your databases are EMPTY**: ✅ **Skip this entirely!** Just start the application normally. New data will automatically use UUIDs.

**If you have EXISTING data with numeric IDs**: ⚠️ **Run this migration once** to convert old data.

---

## Prerequisites (Only if you have existing data)
- Backup MongoDB database
- Ensure all users exist in MySQL with populated UUID fields

## Steps

### 1. Run Migration (Only for existing data)
```bash
# From social-commerce-backend directory
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=migration"
```

### 2. Verify Results
```bash
# Connect to MongoDB
mongosh

# Switch to your database
use socialcommerce

# Check posts
db.posts.findOne()
// Should show: { "authorId": "550e8400-e29b-41d4-a716-446655440000", ... }

# Check comments  
db.comments.findOne()
// Should show: { "authorId": "550e8400-e29b-41d4-a716-446655440000", ... }

# Count migrated documents
db.posts.countDocuments({ "authorId": { $regex: /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i }})
db.comments.countDocuments({ "authorId": { $regex: /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i }})
```

### 3. Start Normal Application
```bash
# Remove migration profile, start normally
./mvnw spring-boot:run
```

### 4. Test Endpoints
```bash
# Create a post (should show populated author name)
curl -X POST http://localhost:8080/api/v1/posts \
  -H "Authorization: Bearer YOUR_JWT" \
  -H "Content-Type: application/json" \
  -d '{"content": "Test post after migration"}'

# Get feed (should show all author names)
curl http://localhost:8080/api/v1/posts/feed \
  -H "Authorization: Bearer YOUR_JWT"

# Add a comment (should succeed)
curl -X POST http://localhost:8080/api/v1/posts/{postId}/comments \
  -H "Authorization: Bearer YOUR_JWT" \
  -H "Content-Type: application/json" \
  -d '{"content": "Test comment"}'
```

## Troubleshooting

### Migration doesn't run
- Ensure profile is active: `--spring.profiles.active=migration`
- Check logs for "Starting MongoDB UUID migration..."

### "No UUID mapping found" warnings
- User exists in MongoDB but not in MySQL
- Either delete the orphaned documents or create the missing user

### Posts/comments still have numeric IDs
- Migration uses regex to detect numeric-only IDs: `^\\d+$`
- If already UUID, migration skips (idempotent)

## Rollback

If something goes wrong:

```bash
# Restore MongoDB from backup
mongorestore --db socialcommerce ./backup/

# Revert code changes
git reset --hard HEAD~1  # Or commit before migration
```

## Success Indicators

✅ Logs show: "MongoDB UUID migration completed successfully!"  
✅ All posts have UUID authorId (contains hyphens)  
✅ All comments have UUID authorId  
✅ Feed shows populated author names  
✅ Comments can be created without errors  
✅ Orders/cart still work (use numeric IDs internally)

## Production Deployment

1. Schedule maintenance window
2. Backup MongoDB
3. Run migration in staging first
4. Deploy new application code
5. Run migration script once
6. Verify all features
7. Monitor error logs for 24h

## Need Help?

See detailed documentation: `MIGRATION_UUID.md`
