# 🚀 Quick Start: Activate Automation in 7 Steps

## Your Automation is Ready! Follow These Steps:

---

### ✅ STEP 1: Database Setup (10 min)
```bash
mysql -u root -p
USE service_request_db;
```

**Run SQL:**
- Create 3 tables: `category_department_mapping`, `sla`, `sla_tracking`
- Insert 4 SLA values (CRITICAL, HIGH, MEDIUM, LOW)
- Map your categories to departments

**Verify:**
```sql
SELECT * FROM sla;
SELECT * FROM category_department_mapping;
```

---

### ✅ STEP 2: Email Config (5 min)

**Get Gmail App Password:**
1. https://myaccount.google.com/security
2. Enable 2-Step Verification
3. Generate App Password
4. Copy password

**Update `application.properties`:**
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

---

### ✅ STEP 3: Rebuild (5 min)

```bash
cd service-request-backend
mvn clean install
```

Wait for "BUILD SUCCESS"

---

### ✅ STEP 4: Add 3 Lines of Code (10 min)

**In `ServiceRequestController.java`:**
```java
@Autowired
private AutoAssignmentService autoAssignmentService;
@Autowired
private SLAService slaService;
@Autowired
private EmailService emailService;

// In createRequest method, add:
autoAssignmentService.autoAssignRequest(request);
slaService.startSLATracking(request);
emailService.sendRequestCreatedEmail(request);
```

**In `ServiceRequestService.java`:**
```java
@Autowired
private ApplicationEventPublisher eventPublisher;

// In updateServiceRequestStatus, add:
RequestStatus oldStatus = request.getStatus();
// ... existing code ...
eventPublisher.publishEvent(
    new RequestStatusChangeEvent(this, saved, oldStatus, newStatus)
);
```

---

### ✅ STEP 5: Start Application (2 min)

```bash
mvn spring-boot:run
```

Look for: "Started ServiceRequestBackendApplication"

---

### ✅ STEP 6: Test (5 min)

**Create a test request** → Should be auto-assigned!

**Check database:**
```sql
SELECT id, title, department_id FROM service_requests ORDER BY id DESC LIMIT 1;
SELECT * FROM sla_tracking ORDER BY id DESC LIMIT 1;
```

**Check email** → Should receive confirmation!

**Check logs** → Look for:
- "Request #X auto-assigned to department: Y"
- "SLA tracking started for request #X"

---

### ✅ STEP 7: Verify Scheduled Tasks (5 min)

**Wait 5 minutes**, then check logs for:
```
INFO - Checking SLA breaches...
```

---

## 🎉 Done! Your System is Automated!

**What's Working:**
- ✅ Auto-assignment to departments
- ✅ SLA tracking with breach detection  
- ✅ Email notifications
- ✅ Auto-close after 7 days
- ✅ Event-driven workflows

---

## 📊 Monitor Success

```sql
-- Auto-assignment rate
SELECT 
    COUNT(*) as total,
    SUM(CASE WHEN department_id IS NOT NULL THEN 1 ELSE 0 END) as assigned,
    ROUND(SUM(CASE WHEN department_id IS NOT NULL THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as rate
FROM service_requests
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY);
```

---

## 🔧 Quick Troubleshooting

| Problem | Solution |
|---------|----------|
| Auto-assignment not working | Check `category_department_mapping` table has data |
| No email received | Verify Gmail app password, check spam folder |
| SLA tracking not created | Check `sla` table has 4 rows |
| Scheduled tasks not running | Restart application, wait 5 minutes |

---

## 📚 Full Guides

- **Detailed Steps:** `STEP_BY_STEP_ACTIVATION.md` ⭐
- **Quick Integration:** `QUICK_INTEGRATION_GUIDE.md`
- **Complete Setup:** `AUTOMATION_SETUP_GUIDE.md`
- **All Features:** `AUTOMATION_COMPLETE.md`

---

**Total Time:** ~40 minutes  
**Your automation is ready to go! 🚀**
