# 🚀 Automation Implementation Examples

## Quick Implementation Guide

This document provides **ready-to-use** code examples for implementing automation in your Service Request Workflow System.

---

## 📋 Example 1: Auto-Assignment Based on Category

### Database Setup

```sql
-- Create category-department mapping table
CREATE TABLE IF NOT EXISTS category_department_mapping (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    is_primary BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES service_categories(id),
    FOREIGN KEY (department_id) REFERENCES departments(id),
    UNIQUE KEY unique_category_dept (category_id, department_id)
);

-- Map categories to departments
INSERT INTO category_department_mapping (category_id, department_id, is_primary)
VALUES 
(1, 1, TRUE),  -- IT Support → IT Department
(2, 3, TRUE),  -- Facilities → Facilities Department
(3, 2, TRUE),  -- HR Request → HR Department
(4, 4, TRUE);  -- General → General Services
```

### Backend Entity

```java
// entity/CategoryDepartmentMapping.java
package com.servicedesk.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "category_department_mapping")
@Data
public class CategoryDepartmentMapping {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ServiceCategory category;
    
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
    
    @Column(name = "is_primary")
    private Boolean isPrimary = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
```

### Repository

```java
// repository/CategoryDepartmentMappingRepository.java
package com.servicedesk.repository;

import com.servicedesk.entity.CategoryDepartmentMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoryDepartmentMappingRepository 
    extends JpaRepository<CategoryDepartmentMapping, Long> {
    
    Optional<CategoryDepartmentMapping> findByCategoryIdAndIsPrimaryTrue(Long categoryId);
}
```

### Service Implementation

```java
// service/AutoAssignmentService.java
package com.servicedesk.service;

import com.servicedesk.entity.*;
import com.servicedesk.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoAssignmentService {
    
    private final CategoryDepartmentMappingRepository mappingRepository;
    private final ServiceRequestRepository requestRepository;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;
    
    /**
     * Auto-assign request to department based on category
     */
    @Transactional
    public void autoAssignRequest(ServiceRequest request) {
        try {
            // Find department mapping
            CategoryDepartmentMapping mapping = mappingRepository
                .findByCategoryIdAndIsPrimaryTrue(request.getCategory().getId())
                .orElseThrow(() -> new RuntimeException(
                    "No department mapping found for category: " + 
                    request.getCategory().getName()
                ));
            
            // Assign to department
            request.setDepartment(mapping.getDepartment());
            request.setStatus(RequestStatus.ASSIGNED);
            requestRepository.save(request);
            
            // Create notification for department
            notificationService.notifyDepartment(
                mapping.getDepartment(),
                "New request assigned: #" + request.getId(),
                "A new " + request.getCategory().getName() + 
                " request has been assigned to your department."
            );
            
            // Log activity
            activityLogService.log(
                request.getUser(),
                "AUTO_ASSIGN",
                "ServiceRequest",
                request.getId(),
                "Auto-assigned to " + mapping.getDepartment().getName()
            );
            
            log.info("Request #{} auto-assigned to {}", 
                request.getId(), 
                mapping.getDepartment().getName()
            );
            
        } catch (Exception e) {
            log.error("Auto-assignment failed for request #{}", request.getId(), e);
            // Don't fail the request creation, just log the error
            request.setStatus(RequestStatus.NEW);
        }
    }
}
```

### Integration in Request Creation

```java
// controller/ServiceRequestController.java
@PostMapping
public ResponseEntity<ServiceRequestDTO> createRequest(
    @RequestBody @Valid ServiceRequestDTO dto,
    @AuthenticationPrincipal UserDetails userDetails
) {
    // Create request
    ServiceRequest request = requestService.createRequest(dto, userDetails);
    
    // Auto-assign to department
    autoAssignmentService.autoAssignRequest(request);
    
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(requestService.toDTO(request));
}
```

---

## 📋 Example 2: SLA Tracking with Auto-Escalation

### Database Setup

```sql
-- SLA definitions table
CREATE TABLE IF NOT EXISTS sla (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    priority VARCHAR(20) NOT NULL,
    category_id BIGINT,
    response_time_minutes INT NOT NULL,
    resolution_time_minutes INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES service_categories(id)
);

-- SLA tracking table
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
    FOREIGN KEY (request_id) REFERENCES service_requests(id),
    FOREIGN KEY (sla_id) REFERENCES sla(id)
);

-- Insert SLA definitions
INSERT INTO sla (priority, response_time_minutes, resolution_time_minutes)
VALUES 
('CRITICAL', 60, 240),      -- 1 hour response, 4 hours resolution
('HIGH', 240, 1440),         -- 4 hours response, 1 day resolution
('MEDIUM', 1440, 4320),      -- 1 day response, 3 days resolution
('LOW', 2880, 10080);        -- 2 days response, 7 days resolution
```

### Entity

```java
// entity/SLATracking.java
package com.servicedesk.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "sla_tracking")
@Data
public class SLATracking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "request_id", nullable = false)
    private ServiceRequest request;
    
    @ManyToOne
    @JoinColumn(name = "sla_id", nullable = false)
    private SLA sla;
    
    @Column(name = "response_due_at", nullable = false)
    private LocalDateTime responseDueAt;
    
    @Column(name = "resolution_due_at", nullable = false)
    private LocalDateTime resolutionDueAt;
    
    @Column(name = "response_met")
    private Boolean responseMet = false;
    
    @Column(name = "resolution_met")
    private Boolean resolutionMet = false;
    
    @Column(name = "breached")
    private Boolean breached = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
```

### Service Implementation

```java
// service/SLAService.java
package com.servicedesk.service;

import com.servicedesk.entity.*;
import com.servicedesk.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SLAService {
    
    private final SLARepository slaRepository;
    private final SLATrackingRepository slaTrackingRepository;
    private final ServiceRequestRepository requestRepository;
    private final NotificationService notificationService;
    
    /**
     * Start SLA tracking for new request
     */
    @Transactional
    public void startSLATracking(ServiceRequest request) {
        try {
            // Find applicable SLA
            SLA sla = slaRepository
                .findByPriorityAndCategoryId(
                    request.getPriority(),
                    request.getCategory().getId()
                )
                .orElseGet(() -> slaRepository
                    .findByPriorityAndCategoryIdIsNull(request.getPriority())
                    .orElseThrow(() -> new RuntimeException("No SLA found"))
                );
            
            // Create tracking record
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
            
            log.info("SLA tracking started for request #{}", request.getId());
            
        } catch (Exception e) {
            log.error("Failed to start SLA tracking for request #{}", 
                request.getId(), e);
        }
    }
    
    /**
     * Check for SLA breaches - runs every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    @Transactional
    public void checkSLABreaches() {
        LocalDateTime now = LocalDateTime.now();
        
        // Find overdue requests
        List<SLATracking> overdue = slaTrackingRepository
            .findByResolutionDueAtBeforeAndBreachedFalse(now);
        
        for (SLATracking tracking : overdue) {
            // Mark as breached
            tracking.setBreached(true);
            slaTrackingRepository.save(tracking);
            
            // Escalate request
            escalateRequest(tracking.getRequest());
            
            // Notify management
            notificationService.notifyManagement(
                "SLA Breach Alert",
                "Request #" + tracking.getRequest().getId() + 
                " has breached SLA. Immediate attention required."
            );
            
            log.warn("SLA breached for request #{}", tracking.getRequest().getId());
        }
        
        // Check for approaching deadlines (1 hour before)
        List<SLATracking> approaching = slaTrackingRepository
            .findByResolutionDueAtBetweenAndBreachedFalse(
                now,
                now.plusHours(1)
            );
        
        for (SLATracking tracking : approaching) {
            notificationService.notifyAssignedUser(
                tracking.getRequest(),
                "SLA Warning",
                "Request #" + tracking.getRequest().getId() + 
                " is approaching SLA deadline in 1 hour."
            );
        }
    }
    
    private void escalateRequest(ServiceRequest request) {
        // Increase priority if not already critical
        if (request.getPriority() != Priority.CRITICAL) {
            Priority oldPriority = request.getPriority();
            request.setPriority(Priority.HIGH);
            requestRepository.save(request);
            
            log.info("Request #{} escalated from {} to {}", 
                request.getId(), oldPriority, Priority.HIGH);
        }
    }
}
```

---

## 📋 Example 3: Email Notifications

### Configuration

```properties
# application.properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Service Implementation

```java
// service/EmailService.java
package com.servicedesk.service;

import com.servicedesk.entity.ServiceRequest;
import com.servicedesk.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    /**
     * Send email asynchronously
     */
    @Async
    public void sendRequestCreatedEmail(ServiceRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(request.getUser().getEmail());
            helper.setSubject("Request #" + request.getId() + " Created Successfully");
            
            // Build email content
            Context context = new Context();
            context.setVariable("request", request);
            context.setVariable("user", request.getUser());
            String htmlContent = templateEngine.process("email/request-created", context);
            
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
            log.info("Email sent to {} for request #{}", 
                request.getUser().getEmail(), 
                request.getId()
            );
            
        } catch (MessagingException e) {
            log.error("Failed to send email for request #{}", request.getId(), e);
        }
    }
    
    @Async
    public void sendStatusChangeEmail(ServiceRequest request, String oldStatus) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(request.getUser().getEmail());
            helper.setSubject("Request #" + request.getId() + " Status Updated");
            
            String htmlContent = buildStatusChangeEmail(request, oldStatus);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
        } catch (MessagingException e) {
            log.error("Failed to send status change email", e);
        }
    }
    
    private String buildStatusChangeEmail(ServiceRequest request, String oldStatus) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #1976d2; color: white; padding: 20px; }
                    .content { padding: 20px; background-color: #f5f5f5; }
                    .button { 
                        background-color: #1976d2; 
                        color: white; 
                        padding: 10px 20px; 
                        text-decoration: none; 
                        border-radius: 5px;
                        display: inline-block;
                        margin-top: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>Request Status Update</h2>
                    </div>
                    <div class="content">
                        <p>Hello %s,</p>
                        <p>Your request <strong>#%d</strong> status has been updated.</p>
                        <p><strong>Previous Status:</strong> %s</p>
                        <p><strong>Current Status:</strong> %s</p>
                        <p><strong>Title:</strong> %s</p>
                        <a href="http://localhost:3000/requests/%d" class="button">
                            View Request Details
                        </a>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                request.getUser().getFirstName(),
                request.getId(),
                oldStatus,
                request.getStatus(),
                request.getTitle(),
                request.getId()
            );
    }
}
```

---

## 📋 Example 4: Scheduled Tasks

### Configuration

```java
// config/SchedulingConfig.java
package com.servicedesk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    // Scheduling enabled
}
```

### Scheduled Tasks Service

```java
// service/ScheduledTasksService.java
package com.servicedesk.service;

import com.servicedesk.entity.*;
import com.servicedesk.repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasksService {
    
    private final ServiceRequestRepository requestRepository;
    private final NotificationService notificationService;
    
    /**
     * Auto-close resolved requests after 7 days
     * Runs daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void autoCloseResolvedRequests() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        
        List<ServiceRequest> requests = requestRepository
            .findByStatusAndResolvedAtBefore(RequestStatus.RESOLVED, cutoff);
        
        for (ServiceRequest request : requests) {
            request.setStatus(RequestStatus.CLOSED);
            request.setClosedAt(LocalDateTime.now());
            requestRepository.save(request);
            
            log.info("Auto-closed request #{}", request.getId());
        }
        
        log.info("Auto-closed {} resolved requests", requests.size());
    }
    
    /**
     * Send daily digest to departments
     * Runs weekdays at 9 AM
     */
    @Scheduled(cron = "0 0 9 * * MON-FRI")
    public void sendDailyDigest() {
        // Implementation for daily digest
        log.info("Sending daily digest to departments");
    }
    
    /**
     * Clean up old notifications
     * Runs weekly on Sunday at 3 AM
     */
    @Scheduled(cron = "0 0 3 * * SUN")
    @Transactional
    public void cleanupOldNotifications() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        // Delete read notifications older than 30 days
        log.info("Cleaning up old notifications");
    }
}
```

---

## 📋 Example 5: Event-Driven Automation

### Event Definition

```java
// event/RequestStatusChangeEvent.java
package com.servicedesk.event;

import com.servicedesk.entity.RequestStatus;
import com.servicedesk.entity.ServiceRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
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
```

### Event Publisher

```java
// service/RequestService.java (excerpt)
@Service
@RequiredArgsConstructor
public class RequestService {
    
    private final ApplicationEventPublisher eventPublisher;
    private final ServiceRequestRepository requestRepository;
    
    @Transactional
    public void updateStatus(Long requestId, RequestStatus newStatus) {
        ServiceRequest request = requestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        
        RequestStatus oldStatus = request.getStatus();
        request.setStatus(newStatus);
        
        if (newStatus == RequestStatus.RESOLVED) {
            request.setResolvedAt(LocalDateTime.now());
        }
        
        requestRepository.save(request);
        
        // Publish event
        eventPublisher.publishEvent(
            new RequestStatusChangeEvent(this, request, oldStatus, newStatus)
        );
    }
}
```

### Event Listeners

```java
// listener/RequestEventListener.java
package com.servicedesk.listener;

import com.servicedesk.event.RequestStatusChangeEvent;
import com.servicedesk.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestEventListener {
    
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final ActivityLogService activityLogService;
    private final SLAService slaService;
    
    /**
     * Handle status change events
     */
    @EventListener
    @Async
    public void handleStatusChange(RequestStatusChangeEvent event) {
        log.info("Processing status change event for request #{}", 
            event.getRequest().getId());
        
        // Send notifications
        notificationService.notifyStatusChange(
            event.getRequest(),
            event.getOldStatus(),
            event.getNewStatus()
        );
        
        // Send email
        emailService.sendStatusChangeEmail(
            event.getRequest(),
            event.getOldStatus().toString()
        );
        
        // Log activity
        activityLogService.logStatusChange(
            event.getRequest(),
            event.getOldStatus(),
            event.getNewStatus()
        );
        
        // Update SLA tracking
        slaService.updateTracking(event.getRequest(), event.getNewStatus());
    }
}
```

---

## 🎯 Testing Automation

### Unit Test Example

```java
// service/AutoAssignmentServiceTest.java
package com.servicedesk.service;

import com.servicedesk.entity.*;
import com.servicedesk.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AutoAssignmentServiceTest {
    
    @Mock
    private CategoryDepartmentMappingRepository mappingRepository;
    
    @Mock
    private ServiceRequestRepository requestRepository;
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private AutoAssignmentService autoAssignmentService;
    
    @Test
    void testAutoAssignRequest_Success() {
        // Arrange
        ServiceCategory category = new ServiceCategory();
        category.setId(1L);
        category.setName("IT Support");
        
        Department department = new Department();
        department.setId(1L);
        department.setName("IT Department");
        
        CategoryDepartmentMapping mapping = new CategoryDepartmentMapping();
        mapping.setCategory(category);
        mapping.setDepartment(department);
        
        ServiceRequest request = new ServiceRequest();
        request.setId(1L);
        request.setCategory(category);
        request.setStatus(RequestStatus.NEW);
        
        when(mappingRepository.findByCategoryIdAndIsPrimaryTrue(1L))
            .thenReturn(Optional.of(mapping));
        
        // Act
        autoAssignmentService.autoAssignRequest(request);
        
        // Assert
        assertEquals(RequestStatus.ASSIGNED, request.getStatus());
        assertEquals(department, request.getDepartment());
        verify(requestRepository, times(1)).save(request);
        verify(notificationService, times(1)).notifyDepartment(any(), any(), any());
    }
}
```

---

## 📊 Monitoring Dashboard Queries

```sql
-- Automation success rate
SELECT 
    DATE(created_at) as date,
    COUNT(*) as total_requests,
    SUM(CASE WHEN department_id IS NOT NULL THEN 1 ELSE 0 END) as auto_assigned,
    ROUND(SUM(CASE WHEN department_id IS NOT NULL THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as success_rate
FROM service_requests
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
GROUP BY DATE(created_at)
ORDER BY date DESC;

-- SLA compliance by priority
SELECT 
    priority,
    COUNT(*) as total,
    SUM(CASE WHEN breached = FALSE THEN 1 ELSE 0 END) as met_sla,
    SUM(CASE WHEN breached = TRUE THEN 1 ELSE 0 END) as breached,
    ROUND(SUM(CASE WHEN breached = FALSE THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as compliance_rate
FROM sla_tracking
GROUP BY priority;

-- Average response time by department
SELECT 
    d.name as department,
    AVG(TIMESTAMPDIFF(MINUTE, sr.created_at, 
        (SELECT MIN(created_at) FROM request_status_history 
         WHERE request_id = sr.id AND new_status = 'IN_PROGRESS'))) as avg_response_minutes
FROM service_requests sr
JOIN departments d ON sr.department_id = d.id
WHERE sr.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY d.name;
```

---

**Last Updated:** December 2025
