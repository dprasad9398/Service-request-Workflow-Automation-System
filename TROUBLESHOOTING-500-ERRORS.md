# Service Catalog 500 Error - Troubleshooting Guide

## Current Issue
Getting 500 Internal Server Error on:
- `GET /api/admin/service-catalog`
- `GET /api/admin/service-catalog/categories`

## Most Likely Causes

### 1. Database Columns Not Added
**Check:** Run `verify-service-catalog-db.sql`
```bash
mysql -u root -p service_request_db < "d:\Final year Project\verify-service-catalog-db.sql"
```

**Expected Output:** Should show 3 columns (default_priority, department, sla_hours)

**If columns are missing:** Run the setup script
```bash
mysql -u root -p service_request_db < "d:\Final year Project\setup-service-catalog.sql"
```

### 2. Backend Not Restarted
**Fix:** Stop and restart Spring Boot application completely

### 3. Backend Error Details
**Check:** Look at backend console for stack trace

Common errors:
- `LazyInitializationException` - Entity relationship issue
- `ColumnNotFoundException` - Database column missing
- `NullPointerException` - Data mapping issue

## Step-by-Step Fix

1. **Stop Backend**
   - Kill the Spring Boot process

2. **Run Database Script**
   ```bash
   mysql -u root -p service_request_db < "d:\Final year Project\setup-service-catalog.sql"
   ```

3. **Verify Database**
   ```bash
   mysql -u root -p service_request_db < "d:\Final year Project\verify-service-catalog-db.sql"
   ```

4. **Start Backend**
   - Run Spring Boot application fresh

5. **Check Backend Logs**
   - Look for any startup errors
   - Verify controllers are registered

6. **Test Endpoints**
   - Try accessing `/api/admin/service-catalog/categories`
   - Check browser console and backend logs

## Alternative: Manual Database Update

If scripts don't work, run these commands manually in MySQL:

```sql
USE service_request_db;

ALTER TABLE service_catalog ADD COLUMN default_priority VARCHAR(20) DEFAULT 'MEDIUM';
ALTER TABLE service_catalog ADD COLUMN department VARCHAR(100);
ALTER TABLE service_catalog ADD COLUMN sla_hours INT;

INSERT INTO service_category (name, description, icon, is_active, created_at, updated_at)
VALUES ('IT Support', 'Information Technology support', 'Computer', 1, NOW(), NOW());
```

## Need Backend Logs

Please share the backend console output (stack trace) to identify the exact error.
