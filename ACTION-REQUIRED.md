# Service Catalog - Action Required

## Current Status
❌ Getting 500 errors on service catalog endpoints

## Root Cause
Missing database columns: `default_priority`, `department`, `sla_hours`

## Fix Required

### Run This SQL (Copy/Paste into MySQL):
```sql
USE service_request_db;

ALTER TABLE service_catalog ADD COLUMN default_priority VARCHAR(20) DEFAULT 'MEDIUM';
ALTER TABLE service_catalog ADD COLUMN department VARCHAR(100);
ALTER TABLE service_catalog ADD COLUMN sla_hours INT;

INSERT INTO service_category (name, description, icon, is_active, created_at, updated_at)
VALUES ('IT Support', 'IT services', 'Computer', 1, NOW(), NOW());
```

### Then:
1. Restart Spring Boot backend
2. Refresh browser
3. Share backend logs if still failing

## Files Created
- `manual-fix.sql` - SQL commands to run
- `setup-service-catalog.sql` - Full setup script
- `TROUBLESHOOTING-500-ERRORS.md` - Detailed guide
