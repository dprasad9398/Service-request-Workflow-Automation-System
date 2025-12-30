# 🏗️ Backend Architecture Guide

## Overview
The backend is built using **Spring Boot 3.x** with a layered architecture pattern, following industry best practices for enterprise Java applications.

---

## 📐 Architecture Layers

```
┌─────────────────────────────────────────────────────────┐
│                  PRESENTATION LAYER                      │
│              (REST API Controllers)                      │
│  • AuthController        • AdminRequestController        │
│  • ServiceRequestController  • DepartmentController      │
│  • UserController        • CategoryController            │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                   SERVICE LAYER                          │
│                (Business Logic)                          │
│  • AuthService           • AdminRequestService           │
│  • ServiceRequestService • DepartmentService             │
│  • UserService           • CategoryService               │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                REPOSITORY LAYER                          │
│              (Data Access Layer)                         │
│  • UserRepository        • ServiceRequestRepository      │
│  • DepartmentRepository  • CategoryRepository            │
│  • RoleRepository        • RequestTypeRepository         │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                   DATABASE LAYER                         │
│                  (MySQL Database)                        │
└─────────────────────────────────────────────────────────┘
```

---

## 📦 Package Structure

```
com.servicedesk/
│
├── 🎮 controller/              # REST API Controllers (19 files)
│   ├── AuthController.java                 - Authentication endpoints
│   ├── ServiceRequestController.java       - User request operations
│   ├── AdminRequestController.java         - Admin request management
│   ├── DepartmentRequestController.java    - Department operations
│   ├── UserController.java                 - User profile management
│   ├── AdminController.java                - Admin user management
│   ├── CategoryController.java             - Category operations
│   ├── AdminCategoryController.java        - Admin category management
│   ├── ServiceCategoryController.java      - Service category API
│   ├── UserServiceCatalogController.java   - User catalog access
│   ├── AdminServiceCatalogController.java  - Admin catalog management
│   ├── DepartmentController.java           - Department CRUD
│   ├── ApprovalController.java             - Approval workflow
│   ├── NotificationController.java         - Notification system
│   ├── ReportsController.java              - Reporting endpoints
│   ├── TaskController.java                 - Task management
│   ├── SystemSettingsController.java       - System configuration
│   ├── DebugController.java                - Debug utilities
│   └── DebugUserController.java            - User debug tools
│
├── 🔧 service/                 # Business Logic Layer
│   ├── AuthService.java                    - Authentication logic
│   ├── ServiceRequestService.java          - Request business logic
│   ├── AdminRequestService.java            - Admin operations
│   ├── DepartmentService.java              - Department logic
│   ├── UserService.java                    - User management
│   ├── CategoryService.java                - Category management
│   ├── ServiceCatalogService.java          - Catalog operations
│   ├── WorkflowService.java                - Workflow engine
│   ├── NotificationService.java            - Notification handling
│   └── EmailService.java                   - Email notifications
│
├── 🗄️ entity/                  # JPA Entities (24 files)
│   ├── User.java                           - User account
│   ├── Role.java                           - User roles
│   ├── ServiceRequest.java                 - Main request entity
│   ├── Department.java                     - Department info
│   ├── ServiceCategory.java                - Request categories
│   ├── RequestType.java                    - Request types
│   ├── ServiceCatalog.java                 - Service catalog
│   ├── RequestComment.java                 - Request comments
│   ├── RequestAttachment.java              - File attachments
│   ├── RequestStatusHistory.java           - Status tracking
│   ├── CategoryDepartmentMapping.java      - Category-dept mapping
│   ├── Workflow.java                       - Workflow definitions
│   ├── WorkflowInstance.java               - Active workflows
│   ├── WorkflowStep.java                   - Workflow steps
│   ├── WorkflowRule.java                   - Workflow rules
│   ├── Approval.java                       - Approval requests
│   ├── ApprovalHistory.java                - Approval tracking
│   ├── Task.java                           - Task management
│   ├── Notification.java                   - User notifications
│   ├── ActivityLog.java                    - Activity tracking
│   ├── AuditLog.java                       - Audit trail
│   ├── SLA.java                            - SLA definitions
│   ├── SLATracking.java                    - SLA monitoring
│   └── SystemSettings.java                 - System config
│
├── 💾 repository/              # Data Access Layer
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   ├── ServiceRequestRepository.java
│   ├── DepartmentRepository.java
│   ├── ServiceCategoryRepository.java
│   ├── RequestTypeRepository.java
│   ├── ServiceCatalogRepository.java
│   ├── RequestCommentRepository.java
│   ├── RequestAttachmentRepository.java
│   ├── RequestStatusHistoryRepository.java
│   ├── WorkflowRepository.java
│   ├── NotificationRepository.java
│   └── AuditLogRepository.java
│
├── 📋 dto/                     # Data Transfer Objects
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── RegisterRequest.java
│   ├── ServiceRequestDTO.java
│   ├── AdminRequestDTO.java
│   ├── DepartmentRequestDTO.java
│   ├── UserDTO.java
│   ├── CategoryDTO.java
│   └── ServiceCatalogDTO.java
│
├── ⚙️ config/                  # Configuration Classes
│   ├── SecurityConfig.java                 - Spring Security setup
│   ├── JwtConfig.java                      - JWT configuration
│   ├── CorsConfig.java                     - CORS settings
│   ├── WebConfig.java                      - Web MVC config
│   └── DatabaseConfig.java                 - Database config
│
├── 🔒 security/                # Security Components
│   ├── JwtTokenProvider.java               - JWT token generation
│   ├── JwtAuthenticationFilter.java        - JWT filter
│   ├── UserDetailsServiceImpl.java         - User details loader
│   └── SecurityUtils.java                  - Security utilities
│
├── ⚠️ exception/               # Exception Handling
│   ├── GlobalExceptionHandler.java         - Global error handler
│   ├── ResourceNotFoundException.java
│   ├── UnauthorizedException.java
│   ├── BadRequestException.java
│   └── ServiceException.java
│
└── 🛠️ util/                    # Utility Classes
    ├── DateUtils.java                      - Date utilities
    ├── StringUtils.java                    - String utilities
    ├── PasswordEncoder.java                - Password hashing
    └── ValidationUtils.java                - Input validation
```

---

## 🔐 Security Architecture

### JWT Authentication Flow

```
1. User Login
   ↓
2. Validate Credentials
   ↓
3. Generate JWT Token (expires in 24h)
   ↓
4. Return Token to Client
   ↓
5. Client Stores Token (localStorage)
   ↓
6. Client Sends Token in Authorization Header
   ↓
7. JwtAuthenticationFilter validates token
   ↓
8. Set SecurityContext with user details
   ↓
9. Process Request
```

### Role-Based Access Control

**Security Configuration:**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    // Public endpoints (no authentication required)
    - /api/auth/login
    - /api/auth/register
    
    // User endpoints (ROLE_USER)
    - /api/requests/**
    - /api/user/**
    
    // Admin endpoints (ROLE_ADMIN)
    - /api/admin/**
    
    // Department endpoints (ROLE_DEPARTMENT)
    - /api/department/**
}
```

---

## 🗃️ Database Entities Explained

### Core Entities

#### **User Entity**
```java
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue
    private Long id;
    
    private String username;      // Unique username
    private String email;          // Unique email
    private String password;       // BCrypt encrypted
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Boolean isActive;      // Account status
    
    @ManyToMany
    private Set<Role> roles;       // User roles
    
    @OneToMany
    private List<ServiceRequest> requests;  // User's requests
}
```

#### **ServiceRequest Entity**
```java
@Entity
@Table(name = "service_requests")
public class ServiceRequest {
    @Id @GeneratedValue
    private Long id;
    
    private String title;
    private String description;
    
    @Enumerated(EnumType.STRING)
    private RequestStatus status;   // NEW, IN_PROGRESS, etc.
    
    @Enumerated(EnumType.STRING)
    private Priority priority;      // LOW, MEDIUM, HIGH, CRITICAL
    
    @ManyToOne
    private User user;              // Request creator
    
    @ManyToOne
    private User assignedTo;        // Assigned agent
    
    @ManyToOne
    private Department department;  // Assigned department
    
    @ManyToOne
    private ServiceCategory category;  // Request category
    
    @ManyToOne
    private RequestType requestType;   // Specific type
    
    @ManyToOne
    private ServiceCatalog service;    // Related service (optional)
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
}
```

#### **Department Entity**
```java
@Entity
@Table(name = "departments")
public class Department {
    @Id @GeneratedValue
    private Long id;
    
    private String name;           // IT, HR, Facilities, etc.
    private String description;
    private String email;
    private Boolean isActive;
    
    @OneToMany
    private List<User> members;    // Department users
    
    @OneToMany
    private List<ServiceRequest> assignedRequests;
}
```

---

## 🔄 Request Lifecycle Management

### Status Transition Flow

```java
public enum RequestStatus {
    NEW,                    // Initial state
    ASSIGNED,               // Assigned to department
    IN_PROGRESS,            // Being worked on
    WAITING_FOR_USER,       // Awaiting user response
    RESOLVED,               // Solution provided
    CLOSED,                 // Completed
    REJECTED                // Denied
}
```

**Allowed Transitions:**
```
NEW → ASSIGNED → IN_PROGRESS → RESOLVED → CLOSED
                      ↓
                WAITING_FOR_USER → IN_PROGRESS
                      
NEW → REJECTED
```

### Priority Levels

```java
public enum Priority {
    LOW,        // Response: 48h, Resolution: 7 days
    MEDIUM,     // Response: 24h, Resolution: 3 days
    HIGH,       // Response: 4h, Resolution: 1 day
    CRITICAL    // Response: 1h, Resolution: 4 hours
}
```

---

## 📡 API Controller Patterns

### Standard CRUD Controller Pattern

```java
@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {
    
    private final ResourceService service;
    
    @GetMapping
    public ResponseEntity<List<ResourceDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ResourceDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @PostMapping
    public ResponseEntity<ResourceDTO> create(@RequestBody ResourceDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(service.create(dto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ResourceDTO> update(
        @PathVariable Long id, 
        @RequestBody ResourceDTO dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## 🔍 Service Layer Patterns

### Service Implementation Pattern

```java
@Service
@RequiredArgsConstructor
@Transactional
public class ResourceServiceImpl implements ResourceService {
    
    private final ResourceRepository repository;
    private final ModelMapper mapper;
    
    @Override
    public List<ResourceDTO> findAll() {
        return repository.findAll().stream()
            .map(entity -> mapper.map(entity, ResourceDTO.class))
            .collect(Collectors.toList());
    }
    
    @Override
    public ResourceDTO findById(Long id) {
        Resource entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return mapper.map(entity, ResourceDTO.class);
    }
    
    @Override
    public ResourceDTO create(ResourceDTO dto) {
        Resource entity = mapper.map(dto, Resource.class);
        Resource saved = repository.save(entity);
        return mapper.map(saved, ResourceDTO.class);
    }
}
```

---

## 🛡️ Exception Handling

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
        ResourceNotFoundException ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
        UnauthorizedException ex
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("Internal server error"));
    }
}
```

---

## 📊 Repository Layer

### Custom Query Examples

```java
@Repository
public interface ServiceRequestRepository 
    extends JpaRepository<ServiceRequest, Long> {
    
    // Find by user
    List<ServiceRequest> findByUserId(Long userId);
    
    // Find by department
    List<ServiceRequest> findByDepartmentId(Long departmentId);
    
    // Find by status
    List<ServiceRequest> findByStatus(RequestStatus status);
    
    // Custom query
    @Query("SELECT r FROM ServiceRequest r WHERE r.status = :status " +
           "AND r.priority = :priority ORDER BY r.createdAt DESC")
    List<ServiceRequest> findByStatusAndPriority(
        @Param("status") RequestStatus status,
        @Param("priority") Priority priority
    );
    
    // Count by department
    @Query("SELECT COUNT(r) FROM ServiceRequest r " +
           "WHERE r.department.id = :deptId AND r.status = :status")
    Long countByDepartmentAndStatus(
        @Param("deptId") Long deptId,
        @Param("status") RequestStatus status
    );
}
```

---

## ⚙️ Configuration Files

### application.properties

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/service_request_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT Configuration
jwt.secret=your-secret-key-here
jwt.expiration=86400000

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging Configuration
logging.level.com.servicedesk=DEBUG
logging.level.org.springframework.security=DEBUG
```

---

## 🧪 Testing Strategy

### Unit Testing
- Test service layer business logic
- Mock repository dependencies
- Use JUnit 5 and Mockito

### Integration Testing
- Test controller endpoints
- Use @SpringBootTest
- Test with H2 in-memory database

### Example Test
```java
@SpringBootTest
@AutoConfigureMockMvc
class ServiceRequestControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testCreateRequest() throws Exception {
        mockMvc.perform(post("/api/requests")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\":\"Test\",\"description\":\"Test\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Test"));
    }
}
```

---

## 📝 Best Practices Implemented

1. **Layered Architecture** - Clear separation of concerns
2. **DTOs** - Prevent entity exposure to clients
3. **Exception Handling** - Centralized error handling
4. **Validation** - Input validation using Bean Validation
5. **Security** - JWT-based authentication
6. **Logging** - Comprehensive logging with SLF4J
7. **Transaction Management** - @Transactional annotations
8. **Code Organization** - Package by feature
9. **Dependency Injection** - Constructor injection with Lombok
10. **RESTful API Design** - Standard HTTP methods and status codes

---

## 🚀 Performance Considerations

- **Database Indexing** - Indexes on frequently queried columns
- **Lazy Loading** - JPA lazy loading for associations
- **Caching** - Spring Cache for frequently accessed data
- **Connection Pooling** - HikariCP for database connections
- **Pagination** - Pageable support for large datasets

---

**Last Updated:** December 2025
