# ✅ Database Tables Exist - Next Steps

## Great! All automation tables are already created.

Now let's verify the data and complete the setup.

---

## Step 1: Verify SLA Values Exist

Run this SQL to check if SLA values are inserted:

```sql
SELECT * FROM sla;
```

**Expected Result:** Should show 4 rows (CRITICAL, HIGH, MEDIUM, LOW)

**If empty, run this:**
```sql
INSERT INTO sla (priority, category_id, response_time_minutes, resolution_time_minutes)
VALUES 
('CRITICAL', NULL, 60, 240),
('HIGH', NULL, 240, 1440),
('MEDIUM', NULL, 1440, 4320),
('LOW', NULL, 2880, 10080)
ON DUPLICATE KEY UPDATE 
    response_time_minutes = VALUES(response_time_minutes),
    resolution_time_minutes = VALUES(resolution_time_minutes);
```

---

## Step 2: Check Your Categories and Departments

```sql
-- See your categories
SELECT id, name FROM service_categories;

-- See your departments  
SELECT id, name FROM departments;
```

**Write down the IDs!** For example:
- Category: IT Support (id=1)
- Department: IT Department (id=1)

---

## Step 3: Create Category-Department Mappings

**IMPORTANT:** Replace the numbers below with YOUR actual IDs from Step 2!

```sql
-- Check if mappings already exist
SELECT 
    c.id as category_id,
    c.name as category,
    d.id as department_id,
    d.name as department
FROM category_department_mapping cdm
JOIN service_categories c ON cdm.category_id = c.id
JOIN departments d ON cdm.department_id = d.id;
```

**If empty or incomplete, add mappings:**
```sql
INSERT INTO category_department_mapping (category_id, department_id, is_primary)
VALUES 
(1, 1, TRUE),  -- REPLACE: Your Category ID → Your Department ID
(2, 2, TRUE),  -- REPLACE: Your Category ID → Your Department ID
(3, 3, TRUE)   -- REPLACE: Your Category ID → Your Department ID
ON DUPLICATE KEY UPDATE is_primary = TRUE;
```

**Example with real data:**
```sql
-- If you have:
-- Category "IT Support" (id=1) → Department "IT Department" (id=1)
-- Category "HR Request" (id=2) → Department "HR Department" (id=2)

INSERT INTO category_department_mapping (category_id, department_id, is_primary)
VALUES 
(1, 1, TRUE),  -- IT Support → IT Department
(2, 2, TRUE)   -- HR Request → HR Department
ON DUPLICATE KEY UPDATE is_primary = TRUE;
```

---

## Step 4: Final Verification

```sql
-- Verify SLA values (should show 4 rows)
SELECT priority, response_time_minutes, resolution_time_minutes FROM sla;

-- Verify mappings (should show your category-department pairs)
SELECT 
    c.name as category,
    d.name as department,
    cdm.is_primary
FROM category_department_mapping cdm
JOIN service_categories c ON cdm.category_id = c.id
JOIN departments d ON cdm.department_id = d.id;

-- Check indexes exist
SHOW INDEX FROM sla_tracking;
SHOW INDEX FROM category_department_mapping;
```

---

## ✅ Once Database is Complete:

**Next Step:** Email Configuration

Open `STEP_BY_STEP_ACTIVATION.md` and go to **Step 2: Email Configuration**

You need to:
1. Get Gmail App Password
2. Update `application.properties`

---

**Current Status:**
- ✅ Tables created
- ⏳ SLA values (verify/insert)
- ⏳ Category-department mappings (verify/create)
- ⏳ Email configuration (next)
