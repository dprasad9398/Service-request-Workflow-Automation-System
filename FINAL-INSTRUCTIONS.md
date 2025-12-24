# FINAL INSTRUCTIONS - Service Catalog Fix

## The Problem
500 errors because database is missing 3 columns

## The Solution (3 Steps)

### Step 1: Open MySQL Workbench
- Connect to your database

### Step 2: Run This SQL
```sql
USE service_request_db;
ALTER TABLE service_catalog ADD COLUMN default_priority VARCHAR(20);
ALTER TABLE service_catalog ADD COLUMN department VARCHAR(100);
ALTER TABLE service_catalog ADD COLUMN sla_hours INT;
```

### Step 3: Restart Backend
- Stop Spring Boot
- Start Spring Boot
- Refresh browser

## That's It!
The Service Catalog will work after these 3 steps.

## All Files Created
- Backend: ServiceCatalogService, AdminServiceCatalogController, UserServiceCatalogController
- Frontend: ServiceCatalogManagement.jsx, Updated CreateRequest.jsx
- DTOs: ServiceCatalogDTO, ServiceCategoryDTO

Everything is ready - just needs the database update.
