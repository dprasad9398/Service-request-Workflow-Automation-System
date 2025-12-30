# 🚀 Next Steps: Email & Code Integration

Database ✅ Complete! Now let's activate the automation.

---

## STEP 2: Email Configuration (5 minutes)

### 2.1 Get Gmail App Password

1. Go to: https://myaccount.google.com/security
2. Click "2-Step Verification" (enable if not already)
3. Scroll to "App passwords"
4. Select "Mail" and "Windows Computer"
5. Click "Generate"
6. **Copy the 16-character password**

### 2.2 Update application.properties

Open: `service-request-backend/src/main/resources/application.properties`

Find lines 34-35 and update:

```properties
spring.mail.username=your-actual-email@gmail.com
spring.mail.password=your-16-char-app-password
```

**Example:**
```properties
spring.mail.username=durga.prasad@gmail.com
spring.mail.password=abcd efgh ijkl mnop
```

✅ Save the file

---

## STEP 3: Rebuild Backend (5 minutes)

Open terminal/command prompt:

```bash
cd "d:\Final year Project\service-request-backend"
mvn clean install
```

Wait for "BUILD SUCCESS"

---

## STEP 4: Add Automation Code (10 minutes)

### 4.1 Update ServiceRequestController

Open: `service-request-backend/src/main/java/com/servicedesk/controller/ServiceRequestController.java`

**Add these 3 autowired fields at the top:**

```java
@Autowired
private AutoAssignmentService autoAssignmentService;

@Autowired
private SLAService slaService;

@Autowired
private EmailService emailService;
```

**Find your `@PostMapping` method for creating requests and add these 3 lines:**

```java
@PostMapping
public ResponseEntity<ServiceRequestDTO> createRequest(...) {
    // Your existing code
    ServiceRequest request = requestService.createServiceRequest(dto, username);
    
    // ✨ ADD THESE 3 LINES:
    autoAssignmentService.autoAssignRequest(request);
    slaService.startSLATracking(request);
    emailService.sendRequestCreatedEmail(request);
    
    return ResponseEntity.ok(toDTO(request));
}
```

### 4.2 Update ServiceRequestService

Open: `service-request-backend/src/main/java/com/servicedesk/service/ServiceRequestService.java`

**Add this field at the top:**

```java
@Autowired
private ApplicationEventPublisher eventPublisher;
```

**Find `updateServiceRequestStatus` method and modify:**

```java
public ServiceRequest updateServiceRequestStatus(Long id, ServiceRequest.RequestStatus newStatus) {
    ServiceRequest request = getServiceRequestById(id);
    
    // ✨ ADD THIS LINE:
    ServiceRequest.RequestStatus oldStatus = request.getStatus();
    
    request.setStatus(newStatus);
    
    if (newStatus == ServiceRequest.RequestStatus.CLOSED) {
        request.setClosedAt(LocalDateTime.now());
    }
    
    if (newStatus == ServiceRequest.RequestStatus.RESOLVED) {
        request.setResolvedAt(LocalDateTime.now());
    }

    ServiceRequest saved = serviceRequestRepository.save(request);
    
    // ✨ ADD THESE LINES:
    eventPublisher.publishEvent(
        new RequestStatusChangeEvent(this, saved, oldStatus, newStatus)
    );

    return saved;
}
```

✅ Save both files

---

## STEP 5: Start Application (2 minutes)

```bash
cd "d:\Final year Project\service-request-backend"
mvn spring-boot:run
```

Look for: "Started ServiceRequestBackendApplication"

---

## STEP 6: Test Automation (5 minutes)

### Create a test request via Postman or frontend

**Check logs for:**
- ✅ "Request #X auto-assigned to department: Y"
- ✅ "SLA tracking started for request #X"
- ✅ "Request created email sent to..."

**Check database:**
```sql
SELECT id, title, department_id FROM service_requests ORDER BY id DESC LIMIT 1;
SELECT * FROM sla_tracking ORDER BY id DESC LIMIT 1;
```

**Check email inbox** - should receive confirmation email!

---

## 🎉 Success!

If you see auto-assignment, SLA tracking, and email - **automation is working!**

---

**Current Step:** Email Configuration  
**Next:** Rebuild → Add Code → Test
