# 📋 Service Request Workflow Automation System - Complete Project Overview

> **Final Year Project** - A comprehensive enterprise-grade service desk management system for handling service requests, workflows, and department coordination.

---

## 🎯 Project Purpose

This system automates the entire lifecycle of service requests in an organization, from submission to resolution. It provides:
- **Self-service portal** for users to submit requests
- **Admin dashboard** for request management and oversight
- **Department dashboards** for assigned request handling
- **Role-based access control** for security
- **Automated workflows** for request routing and escalation

---

## 🏗️ System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    USER INTERFACE LAYER                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ User Portal  │  │Admin Dashboard│  │Dept Dashboard│      │
│  │  (React)     │  │   (React)     │  │   (React)    │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            ↕ HTTP/REST API
┌─────────────────────────────────────────────────────────────┐
│                   APPLICATION LAYER                          │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         Spring Boot Backend (Port 8080)              │   │
│  │  • JWT Authentication    • Request Management        │   │
│  │  • Role-based Security   • Workflow Engine           │   │
│  │  • Business Logic        • Department Routing        │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↕ JPA/Hibernate
┌─────────────────────────────────────────────────────────────┐
│                     DATA LAYER                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              MySQL Database                          │   │
│  │  • Users & Roles    • Service Requests               │   │
│  │  • Departments      • Categories & Types             │   │
│  │  • Workflows        • Audit Logs                     │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
Final year Project/
│
├── 📂 service-request-backend/          # Spring Boot Backend
│   ├── src/main/java/com/servicedesk/
│   │   ├── controller/                  # REST API Controllers (19 files)
│   │   ├── service/                     # Business Logic Layer
│   │   ├── entity/                      # Database Entities (24 models)
│   │   ├── repository/                  # Data Access Layer
│   │   ├── dto/                         # Data Transfer Objects
│   │   ├── config/                      # Spring Configuration
│   │   ├── security/                    # JWT & Security Config
│   │   ├── exception/                   # Exception Handlers
│   │   └── util/                        # Utility Classes
│   ├── src/main/resources/
│   │   └── application.properties       # App Configuration
│   ├── pom.xml                          # Maven Dependencies
│   └── database-schema.sql              # Database Schema
│
├── 📂 service-request-frontend/         # React Frontend
│   ├── src/
│   │   ├── pages/                       # Page Components
│   │   │   ├── admin/                   # Admin Dashboard Pages
│   │   │   ├── department/              # Department Dashboard Pages
│   │   │   └── user/                    # User Portal Pages
│   │   ├── components/                  # Reusable UI Components
│   │   │   ├── admin/                   # Admin-specific Components
│   │   │   └── request/                 # Request-related Components
│   │   ├── services/                    # API Service Layer
│   │   ├── context/                     # React Context (State Management)
│   │   └── api/                         # Axios Configuration
│   ├── package.json                     # NPM Dependencies
│   └── vite.config.js                   # Vite Build Config
│
├── 📂 database-migrations/              # SQL Migration Scripts
│   ├── create-admin-request-management.sql
│   ├── create-request-categories.sql
│   ├── insert-test-requests.sql
│   └── migration_add_category_department.sql
│
├── 📂 scripts/                          # Utility Scripts
│
├── 📄 Documentation Files
│   ├── README.md                        # Main Project README
│   ├── ADMIN-LOGIN-GUIDE.md            # Admin Setup Guide
│   ├── SERVICE-CATALOG-QUICK-START.md  # Service Catalog Guide
│   ├── ROLE_DEPARTMENT_SCRIPTS.md      # Department Role Setup
│   └── DATABASE_CONNECTION_STATUS.md   # DB Connection Guide
│
└── 📄 Utility Batch Files               # Windows Automation Scripts
    ├── start-backend.bat                # Start Spring Boot
    ├── start-frontend.bat               # Start React Dev Server
    ├── setup-database.bat               # Initialize Database
    ├── create-admin-user.bat            # Create Admin User
    └── test-database-connection.bat     # Test DB Connection
```

---

## 🔑 Key Features

### 1. **User Management & Authentication**
- ✅ JWT-based authentication
- ✅ Role-based access control (RBAC)
- ✅ User registration and profile management
- ✅ Password encryption with BCrypt

**Roles:**
- `ROLE_USER` - Submit and track own requests
- `ROLE_ADMIN` - Full system access, user management
- `ROLE_DEPARTMENT` - Handle assigned department requests
- `ROLE_AGENT` - Process and resolve requests
- `ROLE_APPROVER` - Approve/reject requests

### 2. **Service Request Lifecycle**

```
NEW → ASSIGNED → IN_PROGRESS → WAITING_FOR_USER → RESOLVED → CLOSED
                      ↓
                  REJECTED
```

**Request Statuses:**
- `NEW` - Newly created request
- `ASSIGNED` - Assigned to department
- `IN_PROGRESS` - Being worked on
- `WAITING_FOR_USER` - Awaiting user response
- `RESOLVED` - Solution provided
- `CLOSED` - Request completed
- `REJECTED` - Request denied

**Priority Levels:**
- `LOW` - Non-urgent requests
- `MEDIUM` - Standard priority
- `HIGH` - Urgent requests
- `CRITICAL` - Emergency requests

### 3. **Service Categories & Types**

The system supports **4 main categories** with **27+ request types**:

#### 🖥️ IT Support
- Hardware Issues
- Software Installation
- Network Problems
- Email/Account Issues
- Password Reset
- VPN Access
- System Access

#### 🏢 Facilities
- Office Maintenance
- Equipment Request
- Room Booking
- Parking Request
- Security Access
- Cleaning Services

#### 👥 HR Request
- Leave Application
- Attendance Correction
- Payroll Query
- Benefits Enrollment
- Training Request
- Certificate Request
- Employee Verification
- Policy Clarification
- Grievance
- Exit Formalities
- Onboarding Support
- Performance Review
- Transfer Request

#### 📝 General
- Document Request
- Information Request
- Feedback/Suggestion
- Complaint
- Other

### 4. **Admin Dashboard Features**
- 📊 Real-time statistics and metrics
- 👥 User management (CRUD operations)
- 🎫 Request management and assignment
- 🏷️ Category and request type management
- 📋 Service catalog configuration
- 🔄 Status and priority updates
- 🗑️ Request deletion with confirmation
- 📈 Department workload monitoring

### 5. **Department Dashboard Features**
- 📥 View assigned requests
- 🔄 Update request status
- 📝 Add work notes and comments
- ❓ Request user clarification
- ✅ Resolve requests with summary
- 📊 Track department metrics

### 6. **User Portal Features**
- ➕ Create new service requests
- 📋 View personal request history
- 🔍 Track request status
- 💬 Add comments and attachments
- 🔔 Receive notifications
- 👤 Manage profile

---

## 🛠️ Technology Stack

### Backend Technologies
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming Language |
| Spring Boot | 3.x | Application Framework |
| Spring Security | 3.x | Authentication & Authorization |
| Spring Data JPA | 3.x | Database ORM |
| MySQL | 8.0+ | Relational Database |
| Maven | 3.6+ | Build & Dependency Management |
| JWT | - | Token-based Authentication |
| Lombok | - | Reduce Boilerplate Code |
| Hibernate Validator | - | Input Validation |

### Frontend Technologies
| Technology | Version | Purpose |
|------------|---------|---------|
| React | 18 | UI Framework |
| Material-UI (MUI) | 5.x | Component Library |
| React Router | 6.x | Client-side Routing |
| Axios | 1.x | HTTP Client |
| Context API | - | State Management |
| Vite | 4.x | Build Tool & Dev Server |
| date-fns | - | Date Formatting |

---

## 📊 Database Schema Overview

### Core Tables

#### **users**
Stores user account information
- `id`, `username`, `email`, `password` (encrypted)
- `first_name`, `last_name`, `phone_number`
- `is_active`, `created_at`, `updated_at`

#### **roles**
Defines system roles
- `id`, `name` (ROLE_USER, ROLE_ADMIN, etc.)

#### **user_roles**
Many-to-many relationship between users and roles

#### **service_requests**
Main request tracking table
- `id`, `title`, `description`, `status`, `priority`
- `user_id`, `assigned_to_id`, `department_id`
- `category_id`, `request_type_id`, `service_id`
- `created_at`, `updated_at`, `resolved_at`

#### **departments**
Department information
- `id`, `name`, `description`, `is_active`

#### **service_categories**
Request categories (IT, Facilities, HR, General)
- `id`, `name`, `description`, `icon`

#### **request_types**
Specific request types within categories
- `id`, `name`, `category_id`, `description`

#### **service_catalog**
Available services
- `id`, `name`, `description`, `category_id`

### Supporting Tables
- `request_comments` - Comments on requests
- `request_attachments` - File attachments
- `request_status_history` - Status change tracking
- `activity_log` - User activity tracking
- `audit_log` - System audit trail
- `notifications` - User notifications
- `workflows` - Workflow definitions
- `workflow_instances` - Active workflow executions
- `sla_tracking` - SLA monitoring

---

## 🚀 Quick Start Guide

### Prerequisites
- ✅ Java 17 or higher
- ✅ Node.js 16+ and npm
- ✅ MySQL 8.0+
- ✅ Maven 3.6+

### Step 1: Database Setup
```bash
# Create database
mysql -u root -p
CREATE DATABASE service_request_db;
exit;

# Run setup script
cd "d:\Final year Project"
.\setup-database.bat
```

### Step 2: Configure Backend
Edit `service-request-backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/service_request_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### Step 3: Start Backend
```bash
cd "d:\Final year Project"
.\start-backend.bat
```
Backend runs on: `http://localhost:8080`

### Step 4: Start Frontend
```bash
cd "d:\Final year Project"
.\start-frontend.bat
```
Frontend runs on: `http://localhost:3000`

### Step 5: Create Admin User
```bash
.\create-admin-user.bat
```

---

## 📡 API Endpoints Reference

### Authentication Endpoints
```
POST   /api/auth/register          - Register new user
POST   /api/auth/login             - Login and get JWT token
POST   /api/auth/refresh           - Refresh JWT token
GET    /api/auth/me                - Get current user info
```

### User Request Endpoints
```
GET    /api/requests/my-requests   - Get user's own requests
POST   /api/requests               - Create new request
GET    /api/requests/{id}          - Get request details
PUT    /api/requests/{id}          - Update request
DELETE /api/requests/{id}          - Delete request
POST   /api/requests/{id}/comments - Add comment
```

### Admin Endpoints
```
GET    /api/admin/requests         - Get all requests
PUT    /api/admin/requests/{id}/status        - Update status
PATCH  /api/admin/requests/{id}/priority      - Update priority
PUT    /api/admin/requests/{id}/assign        - Assign to department
DELETE /api/admin/requests/{id}               - Delete request
GET    /api/admin/users            - Get all users
POST   /api/admin/users            - Create user
PUT    /api/admin/users/{id}       - Update user
DELETE /api/admin/users/{id}       - Delete user
```

### Department Endpoints
```
GET    /api/department/requests    - Get department's assigned requests
PUT    /api/department/requests/{id}/status   - Update request status
POST   /api/department/requests/{id}/notes    - Add work notes
```

### Category & Service Endpoints
```
GET    /api/service-categories     - Get all categories
GET    /api/service-categories/{id}/types     - Get request types
GET    /api/service-catalog        - Get service catalog
```

---

## 🗂️ Important Files Reference

### Configuration Files
- [`application.properties`](file:///d:/Final%20year%20Project/service-request-backend/src/main/resources/application.properties) - Backend configuration
- [`pom.xml`](file:///d:/Final%20year%20Project/service-request-backend/pom.xml) - Maven dependencies
- [`package.json`](file:///d:/Final%20year%20Project/service-request-frontend/package.json) - NPM dependencies
- [`vite.config.js`](file:///d:/Final%20year%20Project/service-request-frontend/vite.config.js) - Vite configuration

### Key Backend Files
- [`ServiceRequest.java`](file:///d:/Final%20year%20Project/service-request-backend/src/main/java/com/servicedesk/entity/ServiceRequest.java) - Main request entity
- [`User.java`](file:///d:/Final%20year%20Project/service-request-backend/src/main/java/com/servicedesk/entity/User.java) - User entity
- [`AuthController.java`](file:///d:/Final%20year%20Project/service-request-backend/src/main/java/com/servicedesk/controller/AuthController.java) - Authentication API
- [`AdminRequestController.java`](file:///d:/Final%20year%20Project/service-request-backend/src/main/java/com/servicedesk/controller/AdminRequestController.java) - Admin request management

### Key Frontend Files
- [`Login.jsx`](file:///d:/Final%20year%20Project/service-request-frontend/src/pages/Login.jsx) - Login page
- [`AdminDashboard.jsx`](file:///d:/Final%20year%20Project/service-request-frontend/src/pages/admin/AdminDashboard.jsx) - Admin dashboard
- [`DepartmentDashboard.jsx`](file:///d:/Final%20year%20Project/service-request-frontend/src/pages/department/DepartmentDashboard.jsx) - Department dashboard
- [`CreateRequest.jsx`](file:///d:/Final%20year%20Project/service-request-frontend/src/pages/CreateRequest.jsx) - Request creation form

### Documentation Files
- [`README.md`](file:///d:/Final%20year%20Project/README.md) - Main project README
- [`ADMIN-LOGIN-GUIDE.md`](file:///d:/Final%20year%20Project/ADMIN-LOGIN-GUIDE.md) - Admin setup instructions
- [`SERVICE-CATALOG-QUICK-START.md`](file:///d:/Final%20year%20Project/SERVICE-CATALOG-QUICK-START.md) - Service catalog guide
- [`ROLE_DEPARTMENT_SCRIPTS.md`](file:///d:/Final%20year%20Project/ROLE_DEPARTMENT_SCRIPTS.md) - Department role setup

---

## 🔧 Common Operations

### Creating a New Admin User
```bash
.\create-admin-user.bat
```

### Resetting Admin Password
```bash
.\reset-admin-password-final.bat
```

### Testing Database Connection
```bash
.\test-database-connection.bat
```

### Restarting Backend
```bash
.\restart-backend.bat
```

### Setting Up Roles
```bash
.\setup-roles.bat
```

---

## 🐛 Troubleshooting

### Backend Won't Start
1. Check MySQL is running
2. Verify database credentials in `application.properties`
3. Check port 8080 is not in use
4. Run `.\compile-backend.bat` to check for errors

### Frontend Won't Start
1. Run `npm install` in frontend directory
2. Check port 3000 is not in use
3. Verify backend is running on port 8080

### Login Issues
1. Verify user exists in database
2. Check password is correctly hashed
3. Ensure roles are properly assigned
4. Check JWT token configuration

### 404 Errors
1. Verify backend is running
2. Check API endpoint URLs
3. Verify user has correct role permissions

---

## 📈 Recent Improvements

- ✅ Fixed missing request types for Facilities and General categories
- ✅ Added 13 HR-specific request types
- ✅ Implemented department assignment workflow
- ✅ Added department dashboard for ROLE_DEPARTMENT users
- ✅ Fixed category-based request creation flow
- ✅ Resolved null pointer exceptions in workflow triggers
- ✅ Fixed service_id nullable constraint
- ✅ Added admin delete functionality with confirmation
- ✅ Fixed duplicate Facilities category issue

---

## 🎯 Future Enhancements

- [ ] Email notification system
- [ ] SLA tracking and alerts
- [ ] Advanced reporting and analytics
- [ ] Bulk request operations
- [ ] Mobile responsive design improvements
- [ ] File attachment support
- [ ] Request templates
- [ ] Knowledge base integration
- [ ] Chat support integration
- [ ] Multi-language support

---

## 📞 Support & Contact

For questions or issues with this project:
1. Check the troubleshooting guides
2. Review the documentation files
3. Contact the development team

---

## 📄 License

This project is developed as part of academic requirements for a Final Year Project.

---

**Last Updated:** December 2025  
**Version:** 1.0  
**Status:** Active Development
