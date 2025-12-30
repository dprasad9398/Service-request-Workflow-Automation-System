# 📋 Step-by-Step Activation Process

## Complete Guide to Activate Workflow Automation

Follow these steps in order to activate all automation features in your Service Request Workflow Automation System.

---

## 🎯 Overview

**Total Time:** 30-40 minutes  
**Difficulty:** Easy  
**Prerequisites:** MySQL installed, Backend code ready

---

## STEP 1: Database Setup (10 minutes)

### 1.1 Open MySQL Workbench or Command Line

```bash
# Connect to MySQL
mysql -u root -p
# Enter your password: Durga@123
```

### 1.2 Select Your Database

```sql
USE service_request_db;
```

### 1.3 Create Automation Tables

Copy and paste this entire SQL script:

```sql
-- ============================================
-- AUTOMATION TABLES
-- ============================================

-- 1. Category-Department Mapping Table
CREATE TABLE IF NOT EXISTS category_department_mapping (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    is_primary BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES service_categories(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE,
    UNIQUE KEY unique_category_dept (category_id, department_id)
);

-- 2. SLA Definitions Table
CREATE TABLE IF NOT EXISTS sla (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    priority VARCHAR(20) NOT NULL,
    category_id BIGINT NULL,
    response_time_minutes INT NOT NULL,
    resolution_time_minutes INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES service_categories(id) ON DELETE SET NULL,
    UNIQUE KEY unique_priority_category (priority, category_id)
);

-- 3. SLA Tracking Table
CREATE TABLE IF NOT EXISTS sla_tracking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id BIGINT NOT NULL,
    sla_id BIGINT NOT NULL,
    response_due_at TIMESTAMP NOT NULL,
    resolution_due_at TIMESTAMP NOT NULL,
    response_met BOOLEAN DEFAULT FALSE,
    resolution_met BOOLEAN DEFAULT FALSE,
    breached BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (request_id) REFERENCES service_requests(id) ON DELETE CASCADE,
    FOREIGN KEY (sla_id) REFERENCES sla(id) ON DELETE CASCADE,
    UNIQUE KEY unique_request_sla (request_id)
);

-- Create indexes for performance
CREATE INDEX idx_sla_tracking_request ON sla_tracking(request_id);
CREATE INDEX idx_sla_tracking_breach ON sla_tracking(breached, resolution_due_at);
CREATE INDEX idx_category_dept_mapping ON category_department_mapping(category_id, is_primary);
```

### 1.4 Insert Default SLA Values

```sql
-- Insert SLA definitions
INSERT INTO sla (priority, category_id, response_time_minutes, resolution_time_minutes)
VALUES 
('CRITICAL', NULL, 60, 240),      -- 1 hour response, 4 hours resolution
('HIGH', NULL, 240, 1440),         -- 4 hours response, 1 day resolution
('MEDIUM', NULL, 1440, 4320),      -- 1 day response, 3 days resolution
('LOW', NULL, 2880, 10080)         -- 2 days response, 7 days resolution
ON DUPLICATE KEY UPDATE 
    response_time_minutes = VALUES(response_time_minutes),
    resolution_time_minutes = VALUES(resolution_time_minutes);
```

### 1.5 Check Your Existing Categories and Departments

```sql
-- See your categories
SELECT id, name FROM service_categories;

-- See your departments
SELECT id, name FROM departments;
```

**Write down the IDs!** You'll need them for the next step.

### 1.6 Map Categories to Departments

**IMPORTANT:** Replace the numbers below with YOUR actual category and department IDs from step 1.5!

```sql
-- Example: Map categories to departments
-- Format: (category_id, department_id, is_primary)

INSERT INTO category_department_mapping (category_id, department_id, is_primary)
VALUES 
(1, 1, TRUE),  -- Replace: Category ID 1 → Department ID 1
(2, 2, TRUE),  -- Replace: Category ID 2 → Department ID 2
(3, 3, TRUE);  -- Replace: Category ID 3 → Department ID 3

-- Add more mappings as needed for all your categories
```

### 1.7 Verify Database Setup

```sql
-- Check SLA values
SELECT * FROM sla;

-- Check category-department mappings
SELECT 
    c.name as category,
    d.name as department,
    cdm.is_primary
FROM category_department_mapping cdm
JOIN service_categories c ON cdm.category_id = c.id
JOIN departments d ON cdm.department_id = d.id;
```

✅ **Step 1 Complete!** Tables created and data inserted.

---

## STEP 2: Configure Email (5 minutes)

### 2.1 Get Gmail App Password

1. Go to https://myaccount.google.com/security
2. Click on "2-Step Verification" (enable if not already)
3. Scroll down to "App passwords"
4. Click "App passwords"
5. Select "Mail" and "Windows Computer"
6. Click "Generate"
7. **Copy the 16-character password** (e.g., `abcd efgh ijkl mnop`)

### 2.2 Update application.properties

Open: `service-request-backend/src/main/resources/application.properties`

Find these lines (around line 34-35) and update:

```properties
# CHANGE THESE TWO LINES:
spring.mail.username=your-actual-email@gmail.com
spring.mail.password=abcdefghijklmnop
```

**Example:**
```properties
spring.mail.username=durga.prasad@gmail.com
spring.mail.password=abcd efgh ijkl mnop
```

✅ **Step 2 Complete!** Email configured.

---

## STEP 3: Rebuild Backend (5 minutes)

### 3.1 Open Terminal/Command Prompt

Navigate to your backend directory:

```bash
cd "d:\Final year Project\service-request-backend"
```

### 3.2 Clean and Rebuild

```bash
mvn clean install
```

**Wait for:** "BUILD SUCCESS" message

This will:
- Download WebSocket dependency
- Compile all new automation code
- Create the JAR file

✅ **Step 3 Complete!** Backend rebuilt with automation.

---

## STEP 4: Integrate Automation Code (10 minutes)

### 4.1 Find Your ServiceRequestController

Open: `service-request-backend/src/main/java/com/servicedesk/controller/ServiceRequestController.java`

### 4.2 Add Automation Services

**At the top of the class**, add these autowired fields:

```java
@Autowired
private AutoAssignmentService autoAssignmentService;

@Autowired
private SLAService slaService;

@Autowired
private EmailService emailService;
```

### 4.3 Update Create Request Method

Find your `@PostMapping` method that creates requests. Add these 3 lines **AFTER** creating the request:

```java
@PostMapping
public ResponseEntity<ServiceRequestDTO> createRequest(...) {
    // Your existing code to create request
    ServiceRequest request = requestService.createServiceRequest(dto, username);
    
    // ✨ ADD THESE 3 LINES:
    autoAssignmentService.autoAssignRequest(request);
    slaService.startSLATracking(request);
    emailService.sendRequestCreatedEmail(request);
    
    // Your existing return statement
    return ResponseEntity.ok(toDTO(request));
}
```

### 4.4 Update ServiceRequestService for Events

Open: `service-request-backend/src/main/java/com/servicedesk/service/ServiceRequestService.java`

**Add this field at the top:**

```java
@Autowired
private ApplicationEventPublisher eventPublisher;
```

**Find the `updateServiceRequestStatus` method and modify it:**

```java
public ServiceRequest updateServiceRequestStatus(Long id, ServiceRequest.RequestStatus newStatus) {
    ServiceRequest request = getServiceRequestById(id);
    
    // ✨ ADD THIS LINE - Store old status
    ServiceRequest.RequestStatus oldStatus = request.getStatus();
    
    request.setStatus(newStatus);

    if (newStatus == ServiceRequest.RequestStatus.CLOSED) {
        request.setClosedAt(LocalDateTime.now());
    }
    
    if (newStatus == ServiceRequest.RequestStatus.RESOLVED) {
        request.setResolvedAt(LocalDateTime.now());
    }

    ServiceRequest saved = serviceRequestRepository.save(request);
    
    // ✨ ADD THESE LINES - Publish event
    eventPublisher.publishEvent(
        new RequestStatusChangeEvent(this, saved, oldStatus, newStatus)
    );

    return saved;
}
```

✅ **Step 4 Complete!** Automation integrated.

---

## STEP 5: Start the Application (2 minutes)

### 5.1 Run Backend

```bash
cd "d:\Final year Project\service-request-backend"
mvn spring-boot:run
```

### 5.2 Watch for Success Messages

Look for these in the logs:

```
✅ Started ServiceRequestBackendApplication
✅ Tomcat started on port(s): 8080
✅ No errors about missing tables
```

✅ **Step 5 Complete!** Application running with automation.

---

## STEP 6: Test Automation (5 minutes)

### 6.1 Test Auto-Assignment

**Create a test request** (via Postman, frontend, or API):

```json
POST http://localhost:8080/api/requests
{
  "title": "Test Automation",
  "description": "Testing auto-assignment",
  "categoryId": 1,
  "priority": "MEDIUM"
}
```

**Check the response** - should have `departmentId` populated!

### 6.2 Verify in Database

```sql
-- Check if request was auto-assigned
SELECT id, title, department_id, status 
FROM service_requests 
ORDER BY id DESC 
LIMIT 1;

-- Check if SLA tracking was created
SELECT * FROM sla_tracking 
ORDER BY id DESC 
LIMIT 1;
```

### 6.3 Check Logs

Look for these messages in your terminal:

```
✅ "Request #X auto-assigned to department: Y"
✅ "SLA tracking started for request #X"
✅ "Request created email sent to..."
```

### 6.4 Check Email

Check the email inbox for the user who created the request. You should receive a "Request Created Successfully" email!

✅ **Step 6 Complete!** Automation working!

---

## STEP 7: Verify Scheduled Tasks (5 minutes)

### 7.1 Wait 5 Minutes

After starting the application, wait 5 minutes.

### 7.2 Check Logs for SLA Monitoring

Look for this log message (appears every 5 minutes):

```
INFO - Checking SLA breaches...
INFO - Found X requests with SLA breaches
```

If you see this, scheduled tasks are working! ✅

---

## ✅ SUCCESS CHECKLIST

After completing all steps, verify:

- ✅ Database tables created (`category_department_mapping`, `sla`, `sla_tracking`)
- ✅ SLA values inserted (4 rows in `sla` table)
- ✅ Category-department mappings created
- ✅ Email configuration updated
- ✅ Backend rebuilt successfully
- ✅ Automation code integrated
- ✅ Application starts without errors
- ✅ Test request auto-assigned to department
- ✅ SLA tracking record created
- ✅ Email notification received
- ✅ Scheduled tasks running (check logs after 5 min)

---

## 🎉 CONGRATULATIONS!

Your Service Request Workflow Automation System is now **fully automated**!

**What's Automated:**
- ✅ Auto-assignment to departments
- ✅ SLA tracking with breach detection
- ✅ Email notifications
- ✅ Auto-close resolved requests (after 7 days)
- ✅ Event-driven workflows
- ✅ Real-time updates (WebSocket ready)

---

## 📊 Monitor Your Automation

### Check Auto-Assignment Rate

```sql
SELECT 
    COUNT(*) as total_requests,
    SUM(CASE WHEN department_id IS NOT NULL THEN 1 ELSE 0 END) as auto_assigned,
    ROUND(SUM(CASE WHEN department_id IS NOT NULL THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as success_rate
FROM service_requests
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY);
```

### Check SLA Compliance

```sql
SELECT 
    priority,
    COUNT(*) as total,
    SUM(CASE WHEN breached = FALSE THEN 1 ELSE 0 END) as compliant,
    ROUND(SUM(CASE WHEN breached = FALSE THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as compliance_rate
FROM sla_tracking
GROUP BY priority;
```

---

## 🔧 Troubleshooting

### Problem: Auto-assignment not working

**Check:** Do category-department mappings exist?
```sql
SELECT * FROM category_department_mapping;
```

**Fix:** Add mappings for your categories (see Step 1.6)

### Problem: No email received

**Check:** Email configuration correct?
- Verify Gmail app password
- Check spam folder
- Look for errors in logs

**Fix:** Update `application.properties` with correct credentials

### Problem: SLA tracking not created

**Check:** Do SLA definitions exist?
```sql
SELECT * FROM sla;
```

**Fix:** Run Step 1.4 again to insert SLA values

### Problem: Scheduled tasks not running

**Check:** Is `@EnableScheduling` present?
- Open `SchedulingConfig.java`
- Should have `@EnableScheduling` annotation

**Fix:** Already present in the code, just restart application

---

## 📚 Additional Resources

- **Complete Guide:** `AUTOMATION_SETUP_GUIDE.md`
- **Code Examples:** `ServiceRequestControllerExample.java`
- **Integration Guide:** `QUICK_INTEGRATION_GUIDE.md`
- **Full Documentation:** `AUTOMATION_COMPLETE.md`

---

**Need Help?** Check the logs for detailed error messages. Most issues are:
1. Missing database tables (run Step 1)
2. Wrong email credentials (check Step 2)
3. Missing category-department mappings (check Step 1.6)

**Your automation is ready! 🚀**
