## Quick Fix Steps for 500 Errors

### Step 1: Run Database Setup
Open MySQL Workbench or command line and run:

```sql
USE service_request_db;

-- Add missing columns
ALTER TABLE service_catalog ADD COLUMN IF NOT EXISTS default_priority VARCHAR(20) DEFAULT 'MEDIUM';
ALTER TABLE service_catalog ADD COLUMN IF NOT EXISTS department VARCHAR(100);
ALTER TABLE service_catalog ADD COLUMN IF NOT EXISTS sla_hours INT;

-- Add initial categories
INSERT INTO service_category (name, description, icon, is_active, created_at, updated_at)
SELECT 'IT Support', 'IT services', 'Computer', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM service_category WHERE name = 'IT Support');
```

### Step 2: Restart Backend
Stop and restart your Spring Boot application completely.

### Step 3: Test
Navigate to `/admin/service-catalog` and check if errors are gone.

### Step 4: If Still Failing
Please share the backend console error logs.
