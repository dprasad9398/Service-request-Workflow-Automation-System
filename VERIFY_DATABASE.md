# 🎯 Quick Verification Checklist

All tables exist! Now verify the data:

---

## ✅ Run These 3 SQL Queries:

### 1. Check SLA Values
```sql
SELECT * FROM sla;
```
**Expected:** 4 rows (CRITICAL, HIGH, MEDIUM, LOW)

**If empty, insert:**
```sql
INSERT INTO sla (priority, category_id, response_time_minutes, resolution_time_minutes)
VALUES 
('CRITICAL', NULL, 60, 240),
('HIGH', NULL, 240, 1440),
('MEDIUM', NULL, 1440, 4320),
('LOW', NULL, 2880, 10080);
```

---

### 2. Check Your Categories & Departments
```sql
SELECT id, name FROM service_categories;
SELECT id, name FROM departments;
```
**Write down the IDs!**

---

### 3. Check/Create Mappings
```sql
-- Check existing mappings
SELECT c.name as category, d.name as department
FROM category_department_mapping cdm
JOIN service_categories c ON cdm.category_id = c.id
JOIN departments d ON cdm.department_id = d.id;
```

**If empty, create mappings (use YOUR IDs):**
```sql
INSERT INTO category_department_mapping (category_id, department_id, is_primary)
VALUES 
(1, 1, TRUE),  -- Replace with your IDs
(2, 2, TRUE);  -- Add more as needed
```

---

## ✅ Once Data is Verified:

**Next:** Email Configuration (Step 2)

1. Get Gmail App Password: https://myaccount.google.com/security
2. Update `application.properties`:
   ```properties
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   ```

---

**Progress:**
- ✅ Tables created
- ⏳ Data verification (current step)
- ⏳ Email config (next)
