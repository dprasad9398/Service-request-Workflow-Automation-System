# 📂 Project File Organization Guide

## 📋 Overview
This guide explains the organization of all files in the Service Request Workflow Automation System project, helping you quickly locate and understand each file's purpose.

---

## 🗂️ Root Directory Structure

```
d:\Final year Project/
│
├── 📂 service-request-backend/      # Backend Spring Boot application
├── 📂 service-request-frontend/     # Frontend React application
├── 📂 database-migrations/          # Database migration scripts
├── 📂 scripts/                      # Utility scripts
├── 📂 ChatApp/                      # Separate chat application (legacy)
│
├── 📄 Documentation Files           # Project documentation
├── 📄 SQL Scripts                   # Database setup scripts
└── 📄 Batch Files                   # Windows automation scripts
```

---

## 📚 Documentation Files

### Main Documentation (READ THESE FIRST!)

| File | Purpose | When to Use |
|------|---------|-------------|
| [`PROJECT_OVERVIEW.md`](file:///d:/Final%20year%20Project/PROJECT_OVERVIEW.md) | **Complete project overview** with architecture, features, and quick start | Start here for project understanding |
| [`SETUP_GUIDE.md`](file:///d:/Final%20year%20Project/SETUP_GUIDE.md) | **Step-by-step setup instructions** with troubleshooting | Setting up the project for first time |
| [`BACKEND_ARCHITECTURE.md`](file:///d:/Final%20year%20Project/BACKEND_ARCHITECTURE.md) | **Backend architecture details** - layers, patterns, entities | Understanding backend code structure |
| [`FRONTEND_ARCHITECTURE.md`](file:///d:/Final%20year%20Project/FRONTEND_ARCHITECTURE.md) | **Frontend architecture details** - React patterns, components | Understanding frontend code structure |
| [`DATABASE_SCHEMA.md`](file:///d:/Final%20year%20Project/DATABASE_SCHEMA.md) | **Database schema documentation** - tables, relationships, queries | Understanding database design |
| [`README.md`](file:///d:/Final%20year%20Project/README.md) | Original project README with basic info | Quick reference |

### Specialized Guides

| File | Purpose |
|------|---------|
| [`ADMIN-LOGIN-GUIDE.md`](file:///d:/Final%20year%20Project/ADMIN-LOGIN-GUIDE.md) | Admin user creation and login instructions |
| [`SERVICE-CATALOG-QUICK-START.md`](file:///d:/Final%20year%20Project/SERVICE-CATALOG-QUICK-START.md) | Service catalog setup and configuration |
| [`ROLE_DEPARTMENT_SCRIPTS.md`](file:///d:/Final%20year%20Project/ROLE_DEPARTMENT_SCRIPTS.md) | Department role setup instructions |
| [`DATABASE_CONNECTION_STATUS.md`](file:///d:/Final%20year%20Project/DATABASE_CONNECTION_STATUS.md) | Database connection troubleshooting |

### Migration & Status Files

| File | Purpose |
|------|---------|
| [`MIGRATION-SUCCESS.md`](file:///d:/Final%20year%20Project/MIGRATION-SUCCESS.md) | Migration completion status |
| [`STEP-1-COMPLETE.md`](file:///d:/Final%20year%20Project/STEP-1-COMPLETE.md) | Step 1 completion status |
| [`STEP-2-COMPLETE.md`](file:///d:/Final%20year%20Project/STEP-2-COMPLETE.md) | Step 2 completion status |
| [`SERVICE-CATALOG-SUMMARY.md`](file:///d:/Final%20year%20Project/SERVICE-CATALOG-SUMMARY.md) | Service catalog implementation summary |

### Troubleshooting Guides

| File | Purpose |
|------|---------|
| [`TROUBLESHOOTING-500-ERRORS.md`](file:///d:/Final%20year%20Project/TROUBLESHOOTING-500-ERRORS.md) | Fix 500 internal server errors |
| [`TROUBLESHOOTING-REQUEST-TYPES.md`](file:///d:/Final%20year%20Project/TROUBLESHOOTING-REQUEST-TYPES.md) | Fix request type issues |
| [`QUICK-FIX.md`](file:///d:/Final%20year%20Project/QUICK-FIX.md) | Quick fixes for common issues |
| [`ACTION-REQUIRED.md`](file:///d:/Final%20year%20Project/ACTION-REQUIRED.md) | Actions needed after setup |
| [`FINAL-INSTRUCTIONS.md`](file:///d:/Final%20year%20Project/FINAL-INSTRUCTIONS.md) | Final setup instructions |

---

## 🗄️ SQL Scripts

### Database Initialization Scripts

| File | Purpose | When to Run |
|------|---------|-------------|
| [`init-roles.sql`](file:///d:/Final%20year%20Project/init-roles.sql) | Initialize system roles | First-time setup |
| [`create-admin-user.sql`](file:///d:/Final%20year%20Project/create-admin-user.sql) | Create admin user | First-time setup |
| [`create-demo-users.sql`](file:///d:/Final%20year%20Project/create-demo-users.sql) | Create test users | Development/testing |
| [`setup-categories-and-types.sql`](file:///d:/Final%20year%20Project/setup-categories-and-types.sql) | Setup categories and request types | First-time setup |
| [`setup-service-catalog.sql`](file:///d:/Final%20year%20Project/setup-service-catalog.sql) | Setup service catalog | First-time setup |

### Migration Scripts

| File | Purpose |
|------|---------|
| [`admin-request-management-migration.sql`](file:///d:/Final%20year%20Project/admin-request-management-migration.sql) | Admin request management migration |
| [`add-columns.sql`](file:///d:/Final%20year%20Project/add-columns.sql) | Add missing columns |
| [`add-service-catalog-columns.sql`](file:///d:/Final%20year%20Project/add-service-catalog-columns.sql) | Add service catalog columns |

### Fix Scripts

| File | Purpose |
|------|---------|
| [`fix-admin-final.sql`](file:///d:/Final%20year%20Project/fix-admin-final.sql) | Fix admin user issues |
| [`fix-admin-user.sql`](file:///d:/Final%20year%20Project/fix-admin-user.sql) | Fix admin user |
| [`fix-roles-table.sql`](file:///d:/Final%20year%20Project/fix-roles-table.sql) | Fix roles table |
| [`fix-category-display.sql`](file:///d:/Final%20year%20Project/fix-category-display.sql) | Fix category display issues |
| [`fix-category-names.sql`](file:///d:/Final%20year%20Project/fix-category-names.sql) | Fix category names |
| [`fix-duplicate-facilities.sql`](file:///d:/Final%20year%20Project/fix-duplicate-facilities.sql) | Remove duplicate facilities |
| [`fix-missing-request-types.sql`](file:///d:/Final%20year%20Project/fix-missing-request-types.sql) | Add missing request types |
| [`fix-service-id.sql`](file:///d:/Final%20year%20Project/fix-service-id.sql) | Fix service ID issues |
| [`fix.sql`](file:///d:/Final%20year%20Project/fix.sql) | General fixes |
| [`manual-fix.sql`](file:///d:/Final%20year%20Project/manual-fix.sql) | Manual fixes |

### Data Addition Scripts

| File | Purpose |
|------|---------|
| [`add-hr-request-types.sql`](file:///d:/Final%20year%20Project/add-hr-request-types.sql) | Add HR request types |
| [`insert-initial-categories.sql`](file:///d:/Final%20year%20Project/insert-initial-categories.sql) | Insert initial categories |

### Diagnostic Scripts

| File | Purpose |
|------|---------|
| [`check-admin-user.sql`](file:///d:/Final%20year%20Project/check-admin-user.sql) | Check admin user status |
| [`check-request-types.sql`](file:///d:/Final%20year%20Project/check-request-types.sql) | Check request types |
| [`check-service-id.sql`](file:///d:/Final%20year%20Project/check-service-id.sql) | Check service IDs |
| [`diagnose-request-types.sql`](file:///d:/Final%20year%20Project/diagnose-request-types.sql) | Diagnose request type issues |
| [`diagnose-user-requests.sql`](file:///d:/Final%20year%20Project/diagnose-user-requests.sql) | Diagnose user request issues |

### Verification Scripts

| File | Purpose |
|------|---------|
| [`verify-category-migration.sql`](file:///d:/Final%20year%20Project/verify-category-migration.sql) | Verify category migration |
| [`verify-database-state.sql`](file:///d:/Final%20year%20Project/verify-database-state.sql) | Verify database state |
| [`verify-service-catalog-db.sql`](file:///d:/Final%20year%20Project/verify-service-catalog-db.sql) | Verify service catalog |
| [`verify-setup.sql`](file:///d:/Final%20year%20Project/verify-setup.sql) | Verify complete setup |

---

## 🔧 Batch Files (Windows Automation)

### Application Control

| File | Purpose | Usage |
|------|---------|-------|
| [`start-backend.bat`](file:///d:/Final%20year%20Project/start-backend.bat) | **Start Spring Boot backend** | Daily use |
| [`start-frontend.bat`](file:///d:/Final%20year%20Project/start-frontend.bat) | **Start React frontend** | Daily use |
| [`restart-backend.bat`](file:///d:/Final%20year%20Project/restart-backend.bat) | Restart backend server | When needed |
| [`clean-restart-backend.bat`](file:///d:/Final%20year%20Project/clean-restart-backend.bat) | Clean build and restart backend | After code changes |
| [`compile-backend.bat`](file:///d:/Final%20year%20Project/compile-backend.bat) | Compile backend only | Check for errors |

### Database Setup

| File | Purpose |
|------|---------|
| [`setup-database.bat`](file:///d:/Final%20year%20Project/setup-database.bat) | **Complete database setup** |
| [`test-database-connection.bat`](file:///d:/Final%20year%20Project/test-database-connection.bat) | Test database connectivity |
| [`fix-database.bat`](file:///d:/Final%20year%20Project/fix-database.bat) | Fix database issues |

### User Management

| File | Purpose |
|------|---------|
| [`create-admin-user.bat`](file:///d:/Final%20year%20Project/create-admin-user.bat) | **Create admin user** |
| [`create-admin-final.bat`](file:///d:/Final%20year%20Project/create-admin-final.bat) | Create admin (alternative) |
| [`create-admin-user-simple.bat`](file:///d:/Final%20year%20Project/create-admin-user-simple.bat) | Create admin (simple) |
| [`reset-admin-password-final.bat`](file:///d:/Final%20year%20Project/reset-admin-password-final.bat) | **Reset admin password** |
| [`update-admin-password.bat`](file:///d:/Final%20year%20Project/update-admin-password.bat) | Update admin password |
| [`fix-admin-quick.bat`](file:///d:/Final%20year%20Project/fix-admin-quick.bat) | Quick admin fix |
| [`assign-admin-role.bat`](file:///d:/Final%20year%20Project/assign-admin-role.bat) | Assign admin role |

### Category & Service Setup

| File | Purpose |
|------|---------|
| [`setup-request-categories.bat`](file:///d:/Final%20year%20Project/setup-request-categories.bat) | Setup request categories |
| [`setup-categories-and-types.bat`](file:///d:/Final%20year%20Project/setup-categories-and-types.bat) | Setup categories and types |
| [`run-category-migration.bat`](file:///d:/Final%20year%20Project/run-category-migration.bat) | Run category migration |
| [`run-setup-categories.bat`](file:///d:/Final%20year%20Project/run-setup-categories.bat) | Run category setup |
| [`run-fix-categories.bat`](file:///d:/Final%20year%20Project/run-fix-categories.bat) | Fix category issues |
| [`run-fix-duplicate.bat`](file:///d:/Final%20year%20Project/run-fix-duplicate.bat) | Fix duplicate entries |
| [`run-add-hr-types.bat`](file:///d:/Final%20year%20Project/run-add-hr-types.bat) | Add HR request types |
| [`run-fix-request-types.bat`](file:///d:/Final%20year%20Project/run-fix-request-types.bat) | Fix request types |

### Admin & Role Setup

| File | Purpose |
|------|---------|
| [`setup-roles.bat`](file:///d:/Final%20year%20Project/setup-roles.bat) | **Setup system roles** |
| [`setup-admin-request-management.bat`](file:///d:/Final%20year%20Project/setup-admin-request-management.bat) | Setup admin request management |
| [`setup-admin-via-api.bat`](file:///d:/Final%20year%20Project/setup-admin-via-api.bat) | Setup admin via API |

### Testing & Troubleshooting

| File | Purpose |
|------|---------|
| [`test-admin-login.bat`](file:///d:/Final%20year%20Project/test-admin-login.bat) | Test admin login |
| [`troubleshoot-login.bat`](file:///d:/Final%20year%20Project/troubleshoot-login.bat) | Troubleshoot login issues |
| [`check-jwt-token.bat`](file:///d:/Final%20year%20Project/check-jwt-token.bat) | Check JWT token |
| [`insert-test-requests.bat`](file:///d:/Final%20year%20Project/insert-test-requests.bat) | Insert test requests |
| [`apply-fix.bat`](file:///d:/Final%20year%20Project/apply-fix.bat) | Apply general fixes |

---

## 📂 Backend Directory Structure

```
service-request-backend/
│
├── 📂 src/main/java/com/servicedesk/
│   │
│   ├── 📂 controller/              # REST API Controllers
│   │   ├── AuthController.java
│   │   ├── ServiceRequestController.java
│   │   ├── AdminRequestController.java
│   │   ├── DepartmentRequestController.java
│   │   ├── UserController.java
│   │   ├── AdminController.java
│   │   ├── CategoryController.java
│   │   ├── ServiceCategoryController.java
│   │   ├── DepartmentController.java
│   │   └── ... (19 controllers total)
│   │
│   ├── 📂 service/                 # Business Logic
│   │   ├── AuthService.java
│   │   ├── ServiceRequestService.java
│   │   ├── AdminRequestService.java
│   │   ├── DepartmentService.java
│   │   └── ... (service implementations)
│   │
│   ├── 📂 entity/                  # JPA Entities
│   │   ├── User.java
│   │   ├── Role.java
│   │   ├── ServiceRequest.java
│   │   ├── Department.java
│   │   ├── ServiceCategory.java
│   │   ├── RequestType.java
│   │   └── ... (24 entities total)
│   │
│   ├── 📂 repository/              # Data Access Layer
│   │   ├── UserRepository.java
│   │   ├── ServiceRequestRepository.java
│   │   ├── DepartmentRepository.java
│   │   └── ... (repository interfaces)
│   │
│   ├── 📂 dto/                     # Data Transfer Objects
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   ├── ServiceRequestDTO.java
│   │   ├── AdminRequestDTO.java
│   │   └── ... (DTOs)
│   │
│   ├── 📂 config/                  # Configuration
│   │   ├── SecurityConfig.java
│   │   ├── JwtConfig.java
│   │   └── CorsConfig.java
│   │
│   ├── 📂 security/                # Security Components
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── UserDetailsServiceImpl.java
│   │
│   ├── 📂 exception/               # Exception Handling
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   └── UnauthorizedException.java
│   │
│   └── 📂 util/                    # Utilities
│       ├── DateUtils.java
│       └── PasswordEncoder.java
│
├── 📂 src/main/resources/
│   ├── application.properties      # **Main configuration file**
│   └── application-dev.properties  # Development config
│
├── 📄 pom.xml                      # **Maven dependencies**
├── 📄 database-schema.sql          # Database schema
└── 📂 target/                      # Build output (generated)
```

---

## 📂 Frontend Directory Structure

```
service-request-frontend/
│
├── 📂 src/
│   │
│   ├── 📄 App.jsx                  # **Main application component**
│   ├── 📄 main.jsx                 # Application entry point
│   │
│   ├── 📂 pages/                   # Page Components
│   │   │
│   │   ├── 📄 Login.jsx            # **Login page**
│   │   ├── 📄 Register.jsx         # Registration page
│   │   ├── 📄 Dashboard.jsx        # Main dashboard router
│   │   ├── 📄 Unauthorized.jsx     # 403 error page
│   │   │
│   │   ├── 📂 user/                # User Pages
│   │   │   ├── UserDashboard.jsx
│   │   │   ├── CreateRequest.jsx
│   │   │   ├── MyRequests.jsx
│   │   │   ├── RequestDetails.jsx
│   │   │   └── Profile.jsx
│   │   │
│   │   ├── 📂 admin/               # Admin Pages
│   │   │   ├── AdminDashboard.jsx
│   │   │   ├── RequestManagement.jsx
│   │   │   ├── UserManagement.jsx
│   │   │   └── ... (admin pages)
│   │   │
│   │   └── 📂 department/          # Department Pages
│   │       ├── DepartmentDashboard.jsx
│   │       └── AssignedRequests.jsx
│   │
│   ├── 📂 components/              # Reusable Components
│   │   ├── 📂 common/
│   │   │   ├── Navbar.jsx
│   │   │   ├── Sidebar.jsx
│   │   │   └── ProtectedRoute.jsx
│   │   │
│   │   ├── 📂 request/
│   │   │   ├── RequestCard.jsx
│   │   │   ├── RequestList.jsx
│   │   │   ├── StatusBadge.jsx
│   │   │   └── ... (request components)
│   │   │
│   │   └── 📂 admin/
│   │       ├── UserTable.jsx
│   │       ├── UserFormModal.jsx
│   │       └── ... (admin components)
│   │
│   ├── 📂 services/                # API Services
│   │   ├── authService.js          # **Authentication API**
│   │   ├── requestService.js       # Request API
│   │   ├── adminService.js         # Admin API
│   │   └── ... (service files)
│   │
│   ├── 📂 context/                 # React Context
│   │   ├── AuthContext.jsx         # **Authentication state**
│   │   └── RequestContext.jsx      # Request state
│   │
│   ├── 📂 api/                     # API Configuration
│   │   └── axios.js                # **Axios instance config**
│   │
│   └── 📂 styles/                  # Styles
│       └── theme.js                # MUI theme
│
├── 📄 package.json                 # **NPM dependencies**
├── 📄 vite.config.js               # Vite configuration
├── 📄 index.html                   # HTML template
└── 📂 node_modules/                # Dependencies (generated)
```

---

## 🗂️ Database Migrations Directory

```
database-migrations/
├── create-admin-request-management.sql
├── create-request-categories.sql
├── insert-test-requests.sql
└── migration_add_category_department.sql
```

---

## 📝 Quick File Finder

### "I want to..."

**Setup the project:**
1. Read [`SETUP_GUIDE.md`](file:///d:/Final%20year%20Project/SETUP_GUIDE.md)
2. Run [`setup-database.bat`](file:///d:/Final%20year%20Project/setup-database.bat)
3. Run [`start-backend.bat`](file:///d:/Final%20year%20Project/start-backend.bat)
4. Run [`start-frontend.bat`](file:///d:/Final%20year%20Project/start-frontend.bat)

**Understand the architecture:**
- Backend: [`BACKEND_ARCHITECTURE.md`](file:///d:/Final%20year%20Project/BACKEND_ARCHITECTURE.md)
- Frontend: [`FRONTEND_ARCHITECTURE.md`](file:///d:/Final%20year%20Project/FRONTEND_ARCHITECTURE.md)
- Database: [`DATABASE_SCHEMA.md`](file:///d:/Final%20year%20Project/DATABASE_SCHEMA.md)

**Fix login issues:**
1. Run [`troubleshoot-login.bat`](file:///d:/Final%20year%20Project/troubleshoot-login.bat)
2. Check [`ADMIN-LOGIN-GUIDE.md`](file:///d:/Final%20year%20Project/ADMIN-LOGIN-GUIDE.md)
3. Run [`reset-admin-password-final.bat`](file:///d:/Final%20year%20Project/reset-admin-password-final.bat)

**Add new API endpoint:**
1. Create controller in `service-request-backend/src/main/java/com/servicedesk/controller/`
2. Create service in `service-request-backend/src/main/java/com/servicedesk/service/`
3. Update repository if needed

**Add new React page:**
1. Create page in `service-request-frontend/src/pages/`
2. Add route in `App.jsx`
3. Create service call in `service-request-frontend/src/services/`

**Modify database:**
1. Create migration SQL in `database-migrations/`
2. Run migration script
3. Update entity in `service-request-backend/src/main/java/com/servicedesk/entity/`

---

## 🎯 Most Important Files

### Configuration Files (MUST CONFIGURE)
1. [`application.properties`](file:///d:/Final%20year%20Project/service-request-backend/src/main/resources/application.properties) - Database credentials
2. [`pom.xml`](file:///d:/Final%20year%20Project/service-request-backend/pom.xml) - Backend dependencies
3. [`package.json`](file:///d:/Final%20year%20Project/service-request-frontend/package.json) - Frontend dependencies

### Entry Points
1. Backend: `ServiceRequestApplication.java` (main class)
2. Frontend: [`main.jsx`](file:///d:/Final%20year%20Project/service-request-frontend/src/main.jsx)

### Core Business Logic
1. [`ServiceRequest.java`](file:///d:/Final%20year%20Project/service-request-backend/src/main/java/com/servicedesk/entity/ServiceRequest.java) - Main entity
2. [`ServiceRequestService.java`](file:///d:/Final%20year%20Project/service-request-backend/src/main/java/com/servicedesk/service/ServiceRequestService.java) - Business logic
3. [`ServiceRequestController.java`](file:///d:/Final%20year%20Project/service-request-backend/src/main/java/com/servicedesk/controller/ServiceRequestController.java) - API endpoints

---

## 🔍 File Naming Conventions

### Backend
- **Controllers:** `*Controller.java` (e.g., `UserController.java`)
- **Services:** `*Service.java` (e.g., `UserService.java`)
- **Entities:** PascalCase nouns (e.g., `ServiceRequest.java`)
- **Repositories:** `*Repository.java` (e.g., `UserRepository.java`)
- **DTOs:** `*DTO.java` (e.g., `UserDTO.java`)

### Frontend
- **Pages:** PascalCase (e.g., `UserDashboard.jsx`)
- **Components:** PascalCase (e.g., `RequestCard.jsx`)
- **Services:** camelCase with `Service` suffix (e.g., `authService.js`)
- **Context:** PascalCase with `Context` suffix (e.g., `AuthContext.jsx`)

### SQL Scripts
- **Setup:** `setup-*.sql`
- **Fix:** `fix-*.sql`
- **Check:** `check-*.sql` or `diagnose-*.sql`
- **Verify:** `verify-*.sql`

### Batch Files
- **Start:** `start-*.bat`
- **Setup:** `setup-*.bat`
- **Fix:** `fix-*.bat`
- **Test:** `test-*.bat`

---

## 📊 File Count Summary

- **Documentation Files:** 20+
- **SQL Scripts:** 40+
- **Batch Files:** 30+
- **Backend Java Files:** 100+
- **Frontend JSX Files:** 50+
- **Total Project Files:** 200+

---

**Last Updated:** December 2025
