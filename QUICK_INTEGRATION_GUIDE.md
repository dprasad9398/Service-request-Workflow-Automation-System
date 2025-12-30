# 🚀 Quick Integration Guide

## Activate Automation in 3 Steps

This guide shows you exactly how to integrate the automation features into your existing code.

---

## ✅ Step 1: Database Setup (5 minutes)

### Run this SQL script in MySQL:

```sql
-- Create automation tables
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

-- Insert default SLA values
INSERT INTO sla (priority, category_id, response_time_minutes, resolution_time_minutes)
VALUES 
('CRITICAL', NULL, 60, 240),
('HIGH', NULL, 240, 1440),
('MEDIUM', NULL, 1440, 4320),
('LOW', NULL, 2880, 10080);

-- Map your categories to departments (ADJUST IDs!)
-- Example: IT Support category (id=1) → IT Department (id=1)
INSERT INTO category_department_mapping (category_id, department_id, is_primary)
VALUES 
(1, 1, TRUE),  -- Adjust these IDs based on your data
(2, 2, TRUE),
(3, 3, TRUE);

-- Verify setup
SELECT * FROM sla;
SELECT * FROM category_department_mapping;
```

---

## ✅ Step 2: Update Email Configuration (2 minutes)

Your `application.properties` already has email config. Just update these values:

```properties
# Update these lines in application.properties
spring.mail.username=your-actual-email@gmail.com
spring.mail.password=your-app-password-here
```

**Get Gmail App Password:**
1. Go to https://myaccount.google.com/security
2. Enable 2-Step Verification
3. Search for "App passwords"
4. Generate password for "Mail"
5. Copy and paste into `application.properties`

---

## ✅ Step 3: Integrate Automation Code (10 minutes)

### Option A: Quick Integration (Copy-Paste)

**In your existing `ServiceRequestController.java`:**

```java
// 1. Add these autowired services at the top
@Autowired
private AutoAssignmentService autoAssignmentService;

@Autowired
private SLAService slaService;

@Autowired
private EmailService emailService;

// 2. Modify your createRequest method to add these lines AFTER creating the request:
@PostMapping
public ResponseEntity<ServiceRequestDTO> createRequest(...) {
    // Your existing code to create request
    ServiceRequest request = requestService.createServiceRequest(dto, username);
    
    // ADD THESE 3 LINES FOR AUTOMATION:
    autoAssignmentService.autoAssignRequest(request);
    slaService.startSLATracking(request);
    emailService.sendRequestCreatedEmail(request);
    
    return ResponseEntity.ok(toDTO(request));
}
```

**In your existing `ServiceRequestService.java`:**

```java
// 1. Add this autowired field at the top
@Autowired
private ApplicationEventPublisher eventPublisher;

// 2. Modify your updateServiceRequestStatus method:
public ServiceRequest updateServiceRequestStatus(Long id, RequestStatus newStatus) {
    ServiceRequest request = findById(id);
    RequestStatus oldStatus = request.getStatus();  // Store old status
    
    request.setStatus(newStatus);
    // ... your existing code ...
    
    ServiceRequest saved = serviceRequestRepository.save(request);
    
    // ADD THIS LINE TO PUBLISH EVENTS:
    eventPublisher.publishEvent(
        new RequestStatusChangeEvent(this, saved, oldStatus, newStatus)
    );
    
    return saved;
}
```

### Option B: Use Example Files

I've created complete example files:
- `ServiceRequestControllerExample.java` - Full controller with automation
- `ServiceRequestServiceExample.java` - Full service with event publishing

Copy the relevant methods from these files to your existing code.

---

## ✅ Step 4: Rebuild and Test (5 minutes)

```bash
# Navigate to backend directory
cd service-request-backend

# Clean and rebuild (this will download WebSocket dependency)
mvn clean install

# Run the application
mvn spring-boot:run
```

---

## 🧪 Verify Automation is Working

### Test 1: Auto-Assignment
1. Create a new request via API or frontend
2. Check if `department_id` is populated
3. Look for log: `"Request #X auto-assigned to department: Y"`

### Test 2: SLA Tracking
1. After creating request, run:
   ```sql
   SELECT * FROM sla_tracking WHERE request_id = YOUR_REQUEST_ID;
   ```
2. Verify `response_due_at` and `resolution_due_at` are set

### Test 3: Email Notifications
1. Create a request
2. Check your email inbox
3. Should receive "Request Created" email

### Test 4: Scheduled Tasks
1. Wait 5 minutes after starting application
2. Check logs for: `"Found X requests with SLA breaches"`
3. This confirms SLA monitoring is running

---

## 📊 Monitor Automation

### Check Auto-Assignment Success Rate
```sql
SELECT 
    COUNT(*) as total,
    SUM(CASE WHEN department_id IS NOT NULL THEN 1 ELSE 0 END) as assigned,
    ROUND(SUM(CASE WHEN department_id IS NOT NULL THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as rate
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
**Solution:** Check category-department mappings exist:
```sql
SELECT c.name as category, d.name as department 
FROM category_department_mapping cdm
JOIN service_categories c ON cdm.category_id = c.id
JOIN departments d ON cdm.department_id = d.id;
```

### Problem: Emails not sending
**Solution:** 
1. Verify Gmail app password is correct
2. Check firewall isn't blocking port 587
3. Look for errors in logs

### Problem: SLA tracking not starting
**Solution:**
1. Verify SLA table has data: `SELECT * FROM sla;`
2. Check logs for errors
3. Ensure `@EnableScheduling` is in `SchedulingConfig`

---

## ✅ Success Checklist

After integration, you should have:
- ✅ Requests auto-assigned to departments
- ✅ SLA tracking records created
- ✅ Emails sent on request creation
- ✅ Logs showing automation activity
- ✅ Scheduled tasks running every 5 minutes

---

## 📚 Reference Files

- **Setup Details:** `AUTOMATION_SETUP_GUIDE.md`
- **Complete Summary:** `AUTOMATION_COMPLETE.md`
- **Code Examples:** `AUTOMATION_EXAMPLES.md`
- **Example Controller:** `ServiceRequestControllerExample.java`
- **Example Service:** `ServiceRequestServiceExample.java`

---

**Total Time:** ~20 minutes  
**Difficulty:** Easy  
**Result:** Fully automated workflow system! 🚀
