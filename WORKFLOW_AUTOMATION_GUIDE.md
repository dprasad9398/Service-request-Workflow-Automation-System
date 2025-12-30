# 🤖 Workflow Automation Guide

## Overview

The **Service Request Workflow Automation System** is designed to automate the entire lifecycle of service requests, from submission to resolution. This guide explains how automation works, what's already implemented, and how to extend it.

---

## 🎯 What is Automated?

### Current Automation Features

#### 1. **Automatic Request Routing**
When a user creates a request, the system automatically:
- ✅ Assigns category based on request type
- ✅ Routes to appropriate department
- ✅ Sets initial priority based on request type
- ✅ Creates status history entry
- ✅ Generates notifications

#### 2. **Status Workflow Automation**
The system enforces status transitions:
```
NEW → ASSIGNED → IN_PROGRESS → RESOLVED → CLOSED
                      ↓
                WAITING_FOR_USER
```

**Automated Actions:**
- Status changes trigger notifications
- Timeline updates automatically
- Audit logs created
- Email notifications (when configured)

#### 3. **Department Assignment**
- Auto-assign based on category-department mapping
- Load balancing across department members
- Escalation rules for overdue requests

#### 4. **SLA Tracking**
- Automatic SLA timer starts on request creation
- Priority-based response times
- Escalation when SLA breached
- Automated reminders

#### 5. **Notification System**
- Real-time notifications on status changes
- Assignment notifications
- Comment notifications
- Escalation alerts

---

## 🏗️ Automation Architecture

### Workflow Engine Components

```
┌─────────────────────────────────────────────────────────┐
│                  WORKFLOW ENGINE                         │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────┐  │
│  │  Workflow    │───▶│  Workflow    │───▶│ Workflow │  │
│  │ Definitions  │    │   Steps      │    │  Rules   │  │
│  └──────────────┘    └──────────────┘    └──────────┘  │
│         │                    │                   │      │
│         └────────────────────┼───────────────────┘      │
│                              ↓                          │
│                    ┌──────────────────┐                 │
│                    │ Workflow Instance│                 │
│                    │   (Execution)    │                 │
│                    └──────────────────┘                 │
│                              ↓                          │
│         ┌────────────────────┼────────────────────┐     │
│         ↓                    ↓                    ↓     │
│  ┌──────────┐        ┌──────────┐        ┌──────────┐  │
│  │Auto-Assign│        │Notify    │        │Update SLA│  │
│  │Department │        │Users     │        │Tracking  │  │
│  └──────────┘        └──────────┘        └──────────┘  │
└─────────────────────────────────────────────────────────┘
```

---

## 📋 Database Schema for Automation

### Core Automation Tables

#### **workflows**
Defines automation workflows
```sql
CREATE TABLE workflows (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category_id BIGINT,  -- Apply to specific category
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### **workflow_steps**
Steps in each workflow
```sql
CREATE TABLE workflow_steps (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    workflow_id BIGINT NOT NULL,
    step_order INT NOT NULL,
    step_name VARCHAR(100) NOT NULL,
    step_type VARCHAR(50) NOT NULL,  -- ASSIGN, NOTIFY, APPROVE, etc.
    configuration JSON,  -- Step-specific config
    FOREIGN KEY (workflow_id) REFERENCES workflows(id)
);
```

#### **workflow_instances**
Runtime workflow execution
```sql
CREATE TABLE workflow_instances (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id BIGINT NOT NULL,
    workflow_id BIGINT NOT NULL,
    current_step_id BIGINT,
    status VARCHAR(20) DEFAULT 'PENDING',  -- PENDING, IN_PROGRESS, COMPLETED, FAILED
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (request_id) REFERENCES service_requests(id),
    FOREIGN KEY (workflow_id) REFERENCES workflows(id)
);
```

#### **workflow_rules**
Conditional automation rules
```sql
CREATE TABLE workflow_rules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    workflow_id BIGINT NOT NULL,
    rule_name VARCHAR(100) NOT NULL,
    condition_type VARCHAR(50),  -- PRIORITY, CATEGORY, KEYWORD, etc.
    condition_value TEXT,
    action_type VARCHAR(50),  -- ASSIGN, ESCALATE, NOTIFY, etc.
    action_config JSON,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (workflow_id) REFERENCES workflows(id)
);
```

---

## 🔧 Implementing Automation

### 1. **Auto-Assignment Workflow**

#### Backend Implementation

**Create Workflow Service:**
```java
// service/WorkflowService.java
@Service
@RequiredArgsConstructor
public class WorkflowService {
    
    private final WorkflowRepository workflowRepository;
    private final DepartmentRepository departmentRepository;
    private final CategoryDepartmentMappingRepository mappingRepository;
    
    /**
     * Auto-assign request to department based on category
     */
    public void autoAssignRequest(ServiceRequest request) {
        // Find department mapping for category
        CategoryDepartmentMapping mapping = mappingRepository
            .findByCategoryIdAndIsPrimaryTrue(request.getCategory().getId())
            .orElseThrow(() -> new RuntimeException("No department mapping found"));
        
        // Assign to department
        request.setDepartment(mapping.getDepartment());
        request.setStatus(RequestStatus.ASSIGNED);
        
        // Create notification
        notifyDepartment(mapping.getDepartment(), request);
        
        // Log activity
        logActivity(request, "Auto-assigned to " + mapping.getDepartment().getName());
    }
    
    /**
     * Execute workflow for request
     */
    public void executeWorkflow(ServiceRequest request) {
        // Find applicable workflow
        Workflow workflow = workflowRepository
            .findByCategoryIdAndIsActiveTrue(request.getCategory().getId())
            .orElse(null);
        
        if (workflow != null) {
            // Create workflow instance
            WorkflowInstance instance = new WorkflowInstance();
            instance.setRequest(request);
            instance.setWorkflow(workflow);
            instance.setStatus(InstanceStatus.IN_PROGRESS);
            
            // Execute workflow steps
            executeWorkflowSteps(instance);
        }
    }
    
    private void executeWorkflowSteps(WorkflowInstance instance) {
        List<WorkflowStep> steps = workflowStepRepository
            .findByWorkflowIdOrderByStepOrder(instance.getWorkflow().getId());
        
        for (WorkflowStep step : steps) {
            executeStep(instance, step);
        }
    }
    
    private void executeStep(WorkflowInstance instance, WorkflowStep step) {
        switch (step.getStepType()) {
            case "ASSIGN":
                autoAssignRequest(instance.getRequest());
                break;
            case "NOTIFY":
                sendNotification(instance.getRequest(), step.getConfiguration());
                break;
            case "APPROVE":
                createApprovalRequest(instance.getRequest());
                break;
            case "SLA_START":
                startSLATracking(instance.getRequest());
                break;
        }
        
        instance.setCurrentStep(step);
    }
}
```

#### Usage in Controller:
```java
@PostMapping
public ResponseEntity<ServiceRequestDTO> createRequest(@RequestBody ServiceRequestDTO dto) {
    ServiceRequest request = requestService.createRequest(dto);
    
    // Trigger workflow automation
    workflowService.executeWorkflow(request);
    
    return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(request));
}
```

---

### 2. **SLA Automation**

#### SLA Configuration
```java
// entity/SLA.java
@Entity
@Table(name = "sla")
public class SLA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private Priority priority;
    
    private Integer responseTimeMinutes;  // Time to first response
    private Integer resolutionTimeMinutes; // Time to resolution
    
    @ManyToOne
    private ServiceCategory category;
}
```

#### SLA Tracking Service
```java
@Service
public class SLAService {
    
    /**
     * Start SLA tracking for new request
     */
    public void startSLATracking(ServiceRequest request) {
        SLA sla = slaRepository.findByPriorityAndCategory(
            request.getPriority(), 
            request.getCategory()
        ).orElseThrow();
        
        SLATracking tracking = new SLATracking();
        tracking.setRequest(request);
        tracking.setSla(sla);
        tracking.setResponseDueAt(
            LocalDateTime.now().plusMinutes(sla.getResponseTimeMinutes())
        );
        tracking.setResolutionDueAt(
            LocalDateTime.now().plusMinutes(sla.getResolutionTimeMinutes())
        );
        
        slaTrackingRepository.save(tracking);
    }
    
    /**
     * Check for SLA breaches (run as scheduled task)
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void checkSLABreaches() {
        LocalDateTime now = LocalDateTime.now();
        
        // Find overdue requests
        List<SLATracking> overdue = slaTrackingRepository
            .findByResolutionDueAtBeforeAndBreachedFalse(now);
        
        for (SLATracking tracking : overdue) {
            tracking.setBreached(true);
            
            // Escalate request
            escalateRequest(tracking.getRequest());
            
            // Notify management
            notifyManagement(tracking);
        }
    }
}
```

---

### 3. **Notification Automation**

#### Event-Driven Notifications
```java
@Service
public class NotificationService {
    
    /**
     * Send notification on status change
     */
    @EventListener
    public void onStatusChange(RequestStatusChangeEvent event) {
        ServiceRequest request = event.getRequest();
        
        // Notify request creator
        createNotification(
            request.getUser(),
            "Request Status Updated",
            "Your request #" + request.getId() + " is now " + request.getStatus()
        );
        
        // Notify assigned user
        if (request.getAssignedTo() != null) {
            createNotification(
                request.getAssignedTo(),
                "Assigned Request Updated",
                "Request #" + request.getId() + " status changed to " + request.getStatus()
            );
        }
        
        // Send email (if configured)
        emailService.sendStatusChangeEmail(request);
    }
    
    private void createNotification(User user, String title, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType("STATUS_CHANGE");
        notification.setIsRead(false);
        
        notificationRepository.save(notification);
        
        // Send real-time notification via WebSocket
        webSocketService.sendToUser(user.getId(), notification);
    }
}
```

---

### 4. **Rule-Based Automation**

#### Workflow Rules Engine
```java
@Service
public class WorkflowRuleEngine {
    
    /**
     * Apply rules to request
     */
    public void applyRules(ServiceRequest request) {
        List<WorkflowRule> rules = workflowRuleRepository
            .findByIsActiveTrueOrderByPriorityDesc();
        
        for (WorkflowRule rule : rules) {
            if (evaluateCondition(rule, request)) {
                executeAction(rule, request);
            }
        }
    }
    
    private boolean evaluateCondition(WorkflowRule rule, ServiceRequest request) {
        switch (rule.getConditionType()) {
            case "PRIORITY":
                return request.getPriority().toString()
                    .equals(rule.getConditionValue());
                
            case "CATEGORY":
                return request.getCategory().getName()
                    .equals(rule.getConditionValue());
                
            case "KEYWORD":
                return request.getDescription()
                    .toLowerCase()
                    .contains(rule.getConditionValue().toLowerCase());
                
            case "DEPARTMENT":
                return request.getDepartment() != null &&
                    request.getDepartment().getName()
                        .equals(rule.getConditionValue());
                
            default:
                return false;
        }
    }
    
    private void executeAction(WorkflowRule rule, ServiceRequest request) {
        JSONObject config = new JSONObject(rule.getActionConfig());
        
        switch (rule.getActionType()) {
            case "ASSIGN":
                Long deptId = config.getLong("departmentId");
                Department dept = departmentRepository.findById(deptId).orElseThrow();
                request.setDepartment(dept);
                break;
                
            case "ESCALATE":
                request.setPriority(Priority.HIGH);
                notifyManagement(request);
                break;
                
            case "NOTIFY":
                String message = config.getString("message");
                notifyUsers(request, message);
                break;
                
            case "AUTO_CLOSE":
                if (canAutoClose(request)) {
                    request.setStatus(RequestStatus.CLOSED);
                }
                break;
        }
    }
}
```

---

## 🛠️ Automation Tools & Technologies

### 1. **Spring Boot Scheduling**

Enable scheduled tasks for automation:

```java
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // Configuration
}

@Service
public class ScheduledTasks {
    
    /**
     * Check SLA breaches every 5 minutes
     */
    @Scheduled(fixedRate = 300000)
    public void checkSLABreaches() {
        slaService.checkSLABreaches();
    }
    
    /**
     * Auto-close resolved requests after 7 days
     */
    @Scheduled(cron = "0 0 2 * * *") // Daily at 2 AM
    public void autoCloseResolvedRequests() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        List<ServiceRequest> requests = requestRepository
            .findByStatusAndResolvedAtBefore(RequestStatus.RESOLVED, cutoff);
        
        for (ServiceRequest request : requests) {
            request.setStatus(RequestStatus.CLOSED);
            request.setClosedAt(LocalDateTime.now());
            requestRepository.save(request);
        }
    }
    
    /**
     * Send daily digest to departments
     */
    @Scheduled(cron = "0 0 9 * * MON-FRI") // Weekdays at 9 AM
    public void sendDailyDigest() {
        List<Department> departments = departmentRepository.findAll();
        
        for (Department dept : departments) {
            emailService.sendDailyDigest(dept);
        }
    }
}
```

---

### 2. **Spring Events for Decoupling**

Use event-driven architecture:

```java
// Event definition
public class RequestStatusChangeEvent extends ApplicationEvent {
    private final ServiceRequest request;
    private final RequestStatus oldStatus;
    private final RequestStatus newStatus;
    
    public RequestStatusChangeEvent(Object source, ServiceRequest request, 
                                     RequestStatus oldStatus, RequestStatus newStatus) {
        super(source);
        this.request = request;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}

// Event publisher
@Service
public class RequestService {
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    public void updateStatus(Long requestId, RequestStatus newStatus) {
        ServiceRequest request = findById(requestId);
        RequestStatus oldStatus = request.getStatus();
        
        request.setStatus(newStatus);
        requestRepository.save(request);
        
        // Publish event
        eventPublisher.publishEvent(
            new RequestStatusChangeEvent(this, request, oldStatus, newStatus)
        );
    }
}

// Event listeners
@Component
public class RequestEventListeners {
    
    @EventListener
    public void handleStatusChange(RequestStatusChangeEvent event) {
        // Send notifications
        notificationService.onStatusChange(event);
        
        // Update SLA tracking
        slaService.onStatusChange(event);
        
        // Log activity
        activityLogService.logStatusChange(event);
    }
}
```

---

### 3. **WebSocket for Real-Time Updates**

Enable real-time notifications:

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }
}

@Service
public class WebSocketService {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    public void sendToUser(Long userId, Object message) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notifications",
            message
        );
    }
    
    public void broadcastUpdate(Object message) {
        messagingTemplate.convertAndSend("/topic/updates", message);
    }
}
```

**Frontend WebSocket Client:**
```javascript
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const connectWebSocket = (userId) => {
  const socket = new SockJS('http://localhost:8080/ws');
  const stompClient = Stomp.over(socket);
  
  stompClient.connect({}, () => {
    // Subscribe to user-specific notifications
    stompClient.subscribe(`/user/${userId}/queue/notifications`, (message) => {
      const notification = JSON.parse(message.body);
      showNotification(notification);
    });
    
    // Subscribe to broadcast updates
    stompClient.subscribe('/topic/updates', (message) => {
      const update = JSON.parse(message.body);
      handleUpdate(update);
    });
  });
};
```

---

### 4. **Email Automation with Spring Mail**

Configure email notifications:

```java
@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendStatusChangeEmail(ServiceRequest request) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        try {
            helper.setTo(request.getUser().getEmail());
            helper.setSubject("Request #" + request.getId() + " Status Update");
            helper.setText(buildEmailContent(request), true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email", e);
        }
    }
    
    private String buildEmailContent(ServiceRequest request) {
        return """
            <html>
            <body>
                <h2>Request Status Update</h2>
                <p>Your request <strong>#%d</strong> has been updated.</p>
                <p><strong>Status:</strong> %s</p>
                <p><strong>Title:</strong> %s</p>
                <p><a href="http://localhost:3000/requests/%d">View Request</a></p>
            </body>
            </html>
            """.formatted(
                request.getId(),
                request.getStatus(),
                request.getTitle(),
                request.getId()
            );
    }
}
```

---

### 5. **Apache Camel for Complex Workflows** (Advanced)

For complex automation, integrate Apache Camel:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.apache.camel.springboot</groupId>
    <artifactId>camel-spring-boot-starter</artifactId>
    <version>3.20.0</version>
</dependency>
```

```java
@Component
public class RequestWorkflowRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        // Route for new requests
        from("direct:newRequest")
            .log("Processing new request: ${body.id}")
            .to("bean:workflowService?method=autoAssignRequest")
            .to("bean:slaService?method=startSLATracking")
            .to("bean:notificationService?method=notifyDepartment")
            .choice()
                .when(simple("${body.priority} == 'CRITICAL'"))
                    .to("bean:escalationService?method=escalateImmediately")
                .otherwise()
                    .log("Normal priority request")
            .end();
        
        // Route for status updates
        from("direct:statusUpdate")
            .log("Status updated for request: ${body.id}")
            .to("bean:notificationService?method=sendStatusNotification")
            .to("bean:activityLogService?method=logStatusChange");
    }
}
```

---

## 📊 Automation Workflow Examples

### Example 1: IT Support Request Workflow

```sql
-- Create workflow
INSERT INTO workflows (name, description, category_id, is_active)
VALUES ('IT Support Auto-Assignment', 'Automatically assign IT requests', 1, TRUE);

-- Add workflow steps
INSERT INTO workflow_steps (workflow_id, step_order, step_name, step_type, configuration)
VALUES 
(1, 1, 'Assign to IT Department', 'ASSIGN', '{"departmentId": 1}'),
(2, 2, 'Start SLA Tracking', 'SLA_START', '{"priority": "MEDIUM"}'),
(3, 3, 'Notify Department', 'NOTIFY', '{"message": "New IT request assigned"}'),
(4, 4, 'Create Initial Comment', 'COMMENT', '{"text": "Request received and assigned"}');

-- Add automation rules
INSERT INTO workflow_rules (workflow_id, rule_name, condition_type, condition_value, action_type, action_config)
VALUES 
(1, 'Escalate Critical Issues', 'PRIORITY', 'CRITICAL', 'ESCALATE', '{"notifyManagement": true}'),
(1, 'Auto-assign Password Resets', 'KEYWORD', 'password', 'ASSIGN', '{"userId": 5}');
```

### Example 2: HR Request Approval Workflow

```sql
INSERT INTO workflows (name, description, category_id, is_active)
VALUES ('HR Request Approval', 'HR requests require approval', 3, TRUE);

INSERT INTO workflow_steps (workflow_id, step_order, step_name, step_type, configuration)
VALUES 
(2, 1, 'Assign to HR', 'ASSIGN', '{"departmentId": 2}'),
(2, 2, 'Request Manager Approval', 'APPROVE', '{"approverRole": "MANAGER"}'),
(2, 3, 'Notify on Approval', 'NOTIFY', '{"onApproval": true}'),
(2, 4, 'Process Request', 'CUSTOM', '{"action": "processHRRequest"}');
```

---

## 🎯 Automation Best Practices

### 1. **Design Principles**
- ✅ Keep workflows simple and maintainable
- ✅ Use events for loose coupling
- ✅ Make automation configurable
- ✅ Provide manual override options
- ✅ Log all automated actions

### 2. **Error Handling**
```java
@Service
public class WorkflowService {
    
    public void executeWorkflow(ServiceRequest request) {
        try {
            // Execute workflow
            Workflow workflow = findWorkflow(request);
            executeSteps(workflow, request);
        } catch (WorkflowException e) {
            // Log error
            log.error("Workflow execution failed", e);
            
            // Fallback to manual processing
            request.setStatus(RequestStatus.PENDING_MANUAL);
            
            // Notify admin
            notifyAdmin("Workflow failed for request #" + request.getId());
        }
    }
}
```

### 3. **Performance Optimization**
- Use async processing for heavy tasks
- Batch operations where possible
- Cache workflow definitions
- Use database indexes

```java
@Service
public class AsyncWorkflowService {
    
    @Async
    public CompletableFuture<Void> executeWorkflowAsync(ServiceRequest request) {
        executeWorkflow(request);
        return CompletableFuture.completedFuture(null);
    }
}
```

---

## 🚀 Quick Start: Implementing Your First Automation

### Step 1: Create Workflow Definition

```sql
INSERT INTO workflows (name, description, is_active)
VALUES ('Basic Request Automation', 'Auto-assign and notify', TRUE);
```

### Step 2: Add Workflow Steps

```sql
INSERT INTO workflow_steps (workflow_id, step_order, step_name, step_type)
VALUES 
(1, 1, 'Auto-assign Department', 'ASSIGN'),
(1, 2, 'Send Notification', 'NOTIFY'),
(1, 3, 'Start SLA Timer', 'SLA_START');
```

### Step 3: Implement in Code

```java
@Service
public class RequestService {
    
    @Autowired
    private WorkflowService workflowService;
    
    public ServiceRequest createRequest(ServiceRequestDTO dto) {
        ServiceRequest request = new ServiceRequest();
        // ... set properties
        request = requestRepository.save(request);
        
        // Execute automation
        workflowService.executeWorkflow(request);
        
        return request;
    }
}
```

### Step 4: Test Automation

```bash
# Create a test request
curl -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "title": "Test Automation",
    "description": "Testing workflow automation",
    "categoryId": 1,
    "requestTypeId": 1,
    "priority": "MEDIUM"
  }'

# Check if automation executed
# - Request should be auto-assigned to department
# - Notification should be created
# - SLA tracking should start
```

---

## 📈 Monitoring Automation

### Dashboard Metrics

Track automation effectiveness:

```sql
-- Automation success rate
SELECT 
    COUNT(*) as total_requests,
    SUM(CASE WHEN department_id IS NOT NULL THEN 1 ELSE 0 END) as auto_assigned,
    ROUND(SUM(CASE WHEN department_id IS NOT NULL THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as success_rate
FROM service_requests
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY);

-- Average response time
SELECT 
    AVG(TIMESTAMPDIFF(MINUTE, created_at, 
        (SELECT MIN(created_at) FROM request_status_history 
         WHERE request_id = sr.id AND new_status = 'IN_PROGRESS'))) as avg_response_minutes
FROM service_requests sr
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY);

-- SLA compliance
SELECT 
    priority,
    COUNT(*) as total,
    SUM(CASE WHEN breached = FALSE THEN 1 ELSE 0 END) as met_sla,
    ROUND(SUM(CASE WHEN breached = FALSE THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as compliance_rate
FROM sla_tracking
GROUP BY priority;
```

---

## 🔮 Future Automation Enhancements

### Planned Features

1. **AI-Powered Auto-Categorization**
   - Use ML to categorize requests automatically
   - Suggest solutions based on past requests

2. **Chatbot Integration**
   - Auto-respond to common queries
   - Guide users through request creation

3. **Advanced Analytics**
   - Predict request volumes
   - Identify automation opportunities

4. **Integration APIs**
   - Connect with external tools (Slack, Teams)
   - Sync with ITSM platforms

---

**Last Updated:** December 2025  
**Version:** 1.0
