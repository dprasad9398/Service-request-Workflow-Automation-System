# Troubleshooting: Request Types Not Updating

## Issue
Database has 17 HR request types but frontend shows only 3.

## Possible Causes

### 1. Backend Not Running
The backend needs to be running to serve the API requests.

**Check**: Is backend running on port 8080?

**Fix**:
```bash
cd service-request-backend
mvn spring-boot:run
```

### 2. Using Old API Endpoint
The frontend might be calling an old endpoint that returns cached data.

**Check**: Browser console (F12) → Network tab → Look for API call

**Expected**: `GET /api/service-categories/{id}/types`

### 3. Database Connection Issue
Backend might not be connected to the database properly.

**Check**: Backend logs for database errors

### 4. Browser Cache
Browser might be caching old API responses.

**Fix**:
- Hard refresh: **Ctrl + Shift + R** (Windows)
- Clear cache: Settings → Clear browsing data
- Or use Incognito mode

## Quick Diagnostic Steps

### Step 1: Verify Database (✅ Already Done)
```sql
SELECT COUNT(*) FROM request_types 
WHERE category_id = (SELECT id FROM service_category WHERE name LIKE '%HR%');
-- Should return 17
```

### Step 2: Check Backend Status
```bash
# Check if backend is running
curl http://localhost:8080/api/service-categories
```

If this fails → Backend is not running

### Step 3: Test API Directly
```bash
# Get HR category ID first
curl http://localhost:8080/api/service-categories

# Then get request types (replace {id} with HR category ID)
curl http://localhost:8080/api/service-categories/{id}/types
```

Should return 17 types

### Step 4: Check Browser Console
1. Open DevTools (F12)
2. Go to Network tab
3. Click "Back to Categories"
4. Select "HR Request"
5. Look for: `GET /api/service-categories/{id}/types`
6. Check response - should have 17 items

## Most Likely Issue

**Backend is not running** or **not restarted after database changes**.

## Solution

1. **Start/Restart Backend**:
```bash
cd service-request-backend
mvn spring-boot:run
```

2. **Wait for startup** (look for "Started ServiceRequestApplication")

3. **Hard refresh browser** (Ctrl+Shift+R)

4. **Try again**

## Verification

After backend starts, you should see in backend logs:
```
INFO  - === FETCHING REQUEST TYPES FOR CATEGORY {id} ===
INFO  - Found 17 request types for category {id}
```

And in browser console:
```
📡 API Call: GET /api/service-categories/{id}/types
✅ Request types loaded: 17 types
```
