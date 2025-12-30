# 🚀 Automation Setup Guide

## Quick Start: Setting Up Automation

Follow these steps to activate all automation features in your Service Request Workflow Automation System.

---

## Step 1: Database Setup

### Run Database Migration

Execute this SQL script in your MySQL database:

```sql
-- ============================================
-- AUTOMATION TABLES
-- ============================================

-- 1. Category-Department Mapping
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

-- 2. SLA Definitions
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

-- 3. SLA Tracking
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

-- ============================================
-- INSERT DEFAULT DATA
-- ============================================

-- Insert default SLA values
INSERT INTO sla (priority, category_id, response_time_minutes, resolution_time_minutes)
VALUES 
('CRITICAL', NULL, 60, 240),      -- 1 hour response, 4 hours resolution
('HIGH', NULL, 240, 1440),         -- 4 hours response, 1 day resolution
('MEDIUM', NULL, 1440, 4320),      -- 1 day response, 3 days resolution
('LOW', NULL, 2880, 10080)         -- 2 days response, 7 days resolution
ON DUPLICATE KEY UPDATE 
    response_time_minutes = VALUES(response_time_minutes),
    resolution_time_minutes = VALUES(resolution_time_minutes);

-- Map categories to departments
-- IMPORTANT: Adjust category_id and department_id based on your data
INSERT INTO category_department_mapping (category_id, department_id, is_primary)
SELECT c.id, d.id, TRUE
FROM service_categories c
CROSS JOIN departments d
WHERE 
    (c.name = 'IT Support' AND d.name = 'IT Support') OR
    (c.name = 'Facilities' AND d.name = 'Facilities') OR
    (c.name = 'HR Request' AND d.name = 'HR Department') OR
    (c.name = 'General' AND d.name = 'General Services')
ON DUPLICATE KEY UPDATE is_primary = TRUE;
```

---

## Step 2: Email Configuration

### Add to `application.properties`

```properties
# ============================================
# EMAIL CONFIGURATION
# ============================================

# Gmail SMTP Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# SMTP Properties
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# Email Settings
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000

# Optional: Default from address
spring.mail.from=noreply@yourcompany.com
```

### Get Gmail App Password

1. Go to Google Account settings
2. Enable 2-Step Verification
3. Generate App Password
4. Use that password in `spring.mail.password`

---

## Step 3: Add WebSocket Dependency

### Update `pom.xml`

Add this dependency after the mail dependency:

```xml
<!-- WebSocket for Real-Time Updates -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

---

## Step 4: Integration Code

### Option A: Automatic Integration (Recommended)

The automation services are already created. To activate them, just inject and call them in your existing controller:

```java
// In ServiceRequestController.java or similar

@Autowired
private AutoAssignmentService autoAssignmentService;

@Autowired
private SLAService slaService;

@Autowired
private EmailService emailService;

@PostMapping
public ResponseEntity<ServiceRequestDTO> createRequest(
    @RequestBody @Valid ServiceRequestDTO dto,
    @AuthenticationPrincipal UserDetails userDetails
) {
    // Create request (existing code)
    ServiceRequest request = requestService.createRequest(dto, userDetails.getUsername());
    
    // AUTOMATION: Auto-assign to department
    autoAssignmentService.autoAssignRequest(request);
    
    // AUTOMATION: Start SLA tracking
    slaService.startSLATracking(request);
    
    // AUTOMATION: Send creation email
    emailService.sendRequestCreatedEmail(request);
    
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(requestService.toDTO(request));
}
```

### Option B: Event-Driven Integration

For status changes, publish events:

```java
// In ServiceRequestService.java

@Autowired
private ApplicationEventPublisher eventPublisher;

public void updateStatus(Long requestId, RequestStatus newStatus) {
    ServiceRequest request = findById(requestId);
    RequestStatus oldStatus = request.getStatus();
    
    request.setStatus(newStatus);
    requestRepository.save(request);
    
    // Publish event - triggers notifications, logging, SLA updates
    eventPublisher.publishEvent(
        new RequestStatusChangeEvent(this, request, oldStatus, newStatus)
    );
}
```

---

## Step 5: Rebuild and Restart

```bash
# Navigate to backend directory
cd service-request-backend

# Clean and build
mvn clean install

# Run the application
mvn spring-boot:run
```

---

## Step 6: Verify Automation

### Test Auto-Assignment

1. Create a request with a category
2. Check if `department_id` is populated
3. Check if notification was created
4. Check logs for: `"Request #X auto-assigned to department: Y"`

### Test SLA Tracking

1. Create a request
2. Query: `SELECT * FROM sla_tracking WHERE request_id = X;`
3. Verify `response_due_at` and `resolution_due_at` are set
4. Wait 5 minutes and check logs for SLA monitoring

### Test Email Notifications

1. Configure email settings
2. Create a request
3. Check your email for confirmation
4. Change request status
5. Check for status change email

### Test Real-Time Updates

1. Open browser console
2. Connect to WebSocket: `ws://localhost:8080/ws`
3. Create/update a request
4. Verify WebSocket message received

---

## 📊 Monitoring Automation

### Check Automation Status

```sql
-- Auto-assignment success rate
SELECT 
    COUNT(*) as total_requests,
    SUM(CASE WHEN department_id IS NOT NULL THEN 1 ELSE 0 END) as auto_assigned,
    ROUND(SUM(CASE WHEN department_id IS NOT NULL THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as success_rate
FROM service_requests
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY);

-- SLA compliance
SELECT 
    priority,
    COUNT(*) as total,
    SUM(CASE WHEN breached = FALSE THEN 1 ELSE 0 END) as met_sla,
    ROUND(SUM(CASE WHEN breached = FALSE THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as compliance_rate
FROM sla_tracking
GROUP BY priority;

-- Recent automation activity
SELECT 
    sr.id,
    sr.title,
    sr.status,
    d.name as department,
    st.breached as sla_breached,
    sr.created_at
FROM service_requests sr
LEFT JOIN departments d ON sr.department_id = d.id
LEFT JOIN sla_tracking st ON st.request_id = sr.id
ORDER BY sr.created_at DESC
LIMIT 10;
```

### Check Logs

Look for these log messages:

```
INFO - Starting auto-assignment for request #123
INFO - Request #123 auto-assigned to department: IT Support
INFO - SLA tracking started for request #123. Resolution due: 2025-12-30T02:41:53
INFO - Request created email sent to user@example.com for request #123
INFO - Found 2 requests with SLA breaches
INFO - SLA breached for request #456
```

---

## 🔧 Troubleshooting

### Auto-Assignment Not Working

**Problem:** Requests not auto-assigned

**Solution:**
1. Check if category-department mappings exist:
   ```sql
   SELECT * FROM category_department_mapping;
   ```
2. Verify category ID matches:
   ```sql
   SELECT id, name FROM service_categories;
   ```
3. Check logs for errors

### SLA Tracking Not Starting

**Problem:** No SLA tracking records

**Solution:**
1. Verify SLA definitions exist:
   ```sql
   SELECT * FROM sla;
   ```
2. Check if `@EnableScheduling` is present in `SchedulingConfig`
3. Restart application

### Emails Not Sending

**Problem:** No emails received

**Solution:**
1. Verify SMTP configuration in `application.properties`
2. Test Gmail app password
3. Check firewall/antivirus blocking port 587
4. Look for email errors in logs

### WebSocket Not Connecting

**Problem:** Frontend can't connect to WebSocket

**Solution:**
1. Verify WebSocket dependency in `pom.xml`
2. Check if `WebSocketConfig` exists
3. Ensure CORS is configured properly
4. Test connection: `ws://localhost:8080/ws`

---

## ✅ Success Criteria

After setup, you should see:

- ✅ Requests auto-assigned to departments (>95% success rate)
- ✅ SLA tracking records created for all requests
- ✅ Emails sent on request creation and status changes
- ✅ Scheduled tasks running (check logs every 5 minutes)
- ✅ WebSocket connections established
- ✅ Real-time notifications working

---

## 🎓 Next Steps

1. **Customize SLA Times** - Adjust based on your needs
2. **Add More Mappings** - Map all categories to departments
3. **Configure Email Templates** - Customize email HTML
4. **Frontend Integration** - Add WebSocket client
5. **Monitoring Dashboard** - Create automation metrics page

---

**Setup Time:** 15-30 minutes  
**Difficulty:** Easy  
**Support:** Check logs for detailed error messages
