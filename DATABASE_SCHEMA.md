# 🗄️ Database Schema Documentation

## Overview
The system uses **MySQL 8.0+** as the relational database with **JPA/Hibernate** for ORM. The schema is automatically created and updated using Hibernate's `ddl-auto=update` feature.

---

## 📊 Entity Relationship Diagram

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│    users     │───────│  user_roles  │───────│    roles     │
└──────────────┘  1:N  └──────────────┘  N:1  └──────────────┘
       │                                              
       │ 1:N                                          
       ↓                                              
┌──────────────────────────────────────────────────────────┐
│              service_requests                            │
└──────────────────────────────────────────────────────────┘
       │                    │                    │
       │ N:1                │ N:1                │ N:1
       ↓                    ↓                    ↓
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│ departments  │   │service_      │   │request_types │
│              │   │categories    │   │              │
└──────────────┘   └──────────────┘   └──────────────┘
```

---

## 📋 Core Tables

### 1. **users**
Stores user account information and authentication details.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| `username` | VARCHAR(50) | UNIQUE, NOT NULL | Login username |
| `email` | VARCHAR(100) | UNIQUE, NOT NULL | User email address |
| `password` | VARCHAR(255) | NOT NULL | BCrypt encrypted password |
| `first_name` | VARCHAR(50) | NOT NULL | User's first name |
| `last_name` | VARCHAR(50) | NOT NULL | User's last name |
| `phone_number` | VARCHAR(20) | NULL | Contact number |
| `is_active` | BOOLEAN | DEFAULT TRUE | Account status |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Account creation time |
| `updated_at` | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Last update time |

**Indexes:**
- `idx_username` on `username`
- `idx_email` on `email`

**Sample Data:**
```sql
INSERT INTO users (username, email, password, first_name, last_name, is_active)
VALUES ('admin', 'admin@example.com', '$2a$10$...', 'Admin', 'User', 1);
```

---

### 2. **roles**
Defines system roles for access control.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Role identifier |
| `name` | VARCHAR(50) | UNIQUE, NOT NULL | Role name (ROLE_USER, ROLE_ADMIN, etc.) |
| `description` | VARCHAR(255) | NULL | Role description |

**Predefined Roles:**
```sql
INSERT INTO roles (name, description) VALUES
('ROLE_USER', 'Standard user with basic access'),
('ROLE_ADMIN', 'Administrator with full system access'),
('ROLE_DEPARTMENT', 'Department user for handling assigned requests'),
('ROLE_AGENT', 'Service agent for processing requests'),
('ROLE_APPROVER', 'Approver for request approvals');
```

---

### 3. **user_roles**
Many-to-many relationship between users and roles.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `user_id` | BIGINT | FOREIGN KEY → users(id) | User reference |
| `role_id` | BIGINT | FOREIGN KEY → roles(id) | Role reference |

**Primary Key:** Composite (`user_id`, `role_id`)

---

### 4. **service_requests**
Main table for tracking service requests.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Request identifier |
| `title` | VARCHAR(200) | NOT NULL | Request title |
| `description` | TEXT | NOT NULL | Detailed description |
| `status` | VARCHAR(50) | NOT NULL | Request status (NEW, IN_PROGRESS, etc.) |
| `priority` | VARCHAR(20) | NOT NULL | Priority level (LOW, MEDIUM, HIGH, CRITICAL) |
| `user_id` | BIGINT | FOREIGN KEY → users(id), NOT NULL | Request creator |
| `assigned_to_id` | BIGINT | FOREIGN KEY → users(id), NULL | Assigned agent |
| `department_id` | BIGINT | FOREIGN KEY → departments(id), NULL | Assigned department |
| `category_id` | BIGINT | FOREIGN KEY → service_categories(id), NOT NULL | Request category |
| `request_type_id` | BIGINT | FOREIGN KEY → request_types(id), NOT NULL | Specific request type |
| `service_id` | BIGINT | FOREIGN KEY → service_catalog(id), NULL | Related service (optional) |
| `resolution_summary` | TEXT | NULL | Resolution details |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation time |
| `updated_at` | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Last update time |
| `resolved_at` | TIMESTAMP | NULL | Resolution time |
| `closed_at` | TIMESTAMP | NULL | Closure time |

**Indexes:**
- `idx_user_id` on `user_id`
- `idx_department_id` on `department_id`
- `idx_status` on `status`
- `idx_priority` on `priority`
- `idx_created_at` on `created_at`

**Status Values:**
- `NEW` - Newly created
- `ASSIGNED` - Assigned to department
- `IN_PROGRESS` - Being worked on
- `WAITING_FOR_USER` - Awaiting user response
- `RESOLVED` - Solution provided
- `CLOSED` - Completed
- `REJECTED` - Denied

**Priority Values:**
- `LOW` - Non-urgent
- `MEDIUM` - Standard priority
- `HIGH` - Urgent
- `CRITICAL` - Emergency

---

### 5. **departments**
Department information for request routing.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Department identifier |
| `name` | VARCHAR(100) | UNIQUE, NOT NULL | Department name |
| `description` | TEXT | NULL | Department description |
| `email` | VARCHAR(100) | NULL | Department contact email |
| `is_active` | BOOLEAN | DEFAULT TRUE | Department status |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation time |

**Sample Data:**
```sql
INSERT INTO departments (name, description, email, is_active) VALUES
('IT Support', 'Information Technology support services', 'it@example.com', 1),
('HR Department', 'Human Resources services', 'hr@example.com', 1),
('Facilities', 'Facilities and maintenance services', 'facilities@example.com', 1),
('General Services', 'General administrative services', 'general@example.com', 1);
```

---

### 6. **service_categories**
Main categories for service requests.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Category identifier |
| `name` | VARCHAR(100) | UNIQUE, NOT NULL | Category name |
| `description` | TEXT | NULL | Category description |
| `icon` | VARCHAR(50) | NULL | Icon identifier for UI |
| `is_active` | BOOLEAN | DEFAULT TRUE | Category status |

**Default Categories:**
```sql
INSERT INTO service_categories (name, description, icon, is_active) VALUES
('IT Support', 'Technology and system support', 'computer', 1),
('Facilities', 'Office and facility services', 'building', 1),
('HR Request', 'Human resources services', 'people', 1),
('General', 'General administrative requests', 'help', 1);
```

---

### 7. **request_types**
Specific types within each category.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Type identifier |
| `name` | VARCHAR(100) | NOT NULL | Type name |
| `category_id` | BIGINT | FOREIGN KEY → service_categories(id), NOT NULL | Parent category |
| `description` | TEXT | NULL | Type description |
| `is_active` | BOOLEAN | DEFAULT TRUE | Type status |

**Sample Data:**
```sql
-- IT Support Types
INSERT INTO request_types (name, category_id, is_active) VALUES
('Hardware Issues', 1, 1),
('Software Installation', 1, 1),
('Network Problems', 1, 1),
('Email/Account Issues', 1, 1),
('Password Reset', 1, 1);

-- Facilities Types
INSERT INTO request_types (name, category_id, is_active) VALUES
('Office Maintenance', 2, 1),
('Equipment Request', 2, 1),
('Room Booking', 2, 1);

-- HR Request Types
INSERT INTO request_types (name, category_id, is_active) VALUES
('Leave Application', 3, 1),
('Payroll Query', 3, 1),
('Training Request', 3, 1);
```

---

### 8. **service_catalog**
Available services that can be requested.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Service identifier |
| `name` | VARCHAR(100) | NOT NULL | Service name |
| `description` | TEXT | NULL | Service description |
| `category_id` | BIGINT | FOREIGN KEY → service_categories(id), NOT NULL | Service category |
| `is_active` | BOOLEAN | DEFAULT TRUE | Service availability |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation time |

---

## 📝 Supporting Tables

### 9. **request_comments**
Comments and notes on requests.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Comment identifier |
| `request_id` | BIGINT | FOREIGN KEY → service_requests(id), NOT NULL | Parent request |
| `user_id` | BIGINT | FOREIGN KEY → users(id), NOT NULL | Comment author |
| `comment_text` | TEXT | NOT NULL | Comment content |
| `is_internal` | BOOLEAN | DEFAULT FALSE | Internal note flag |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Comment time |

---

### 10. **request_attachments**
File attachments for requests.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Attachment identifier |
| `request_id` | BIGINT | FOREIGN KEY → service_requests(id), NOT NULL | Parent request |
| `file_name` | VARCHAR(255) | NOT NULL | Original filename |
| `file_path` | VARCHAR(500) | NOT NULL | Storage path |
| `file_size` | BIGINT | NOT NULL | File size in bytes |
| `file_type` | VARCHAR(100) | NOT NULL | MIME type |
| `uploaded_by` | BIGINT | FOREIGN KEY → users(id), NOT NULL | Uploader |
| `uploaded_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Upload time |

---

### 11. **request_status_history**
Tracks status changes for audit trail.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | History identifier |
| `request_id` | BIGINT | FOREIGN KEY → service_requests(id), NOT NULL | Parent request |
| `old_status` | VARCHAR(50) | NULL | Previous status |
| `new_status` | VARCHAR(50) | NOT NULL | New status |
| `changed_by` | BIGINT | FOREIGN KEY → users(id), NOT NULL | User who changed |
| `change_reason` | TEXT | NULL | Reason for change |
| `changed_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Change time |

---

### 12. **category_department_mapping**
Maps categories to departments for auto-routing.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Mapping identifier |
| `category_id` | BIGINT | FOREIGN KEY → service_categories(id), NOT NULL | Category |
| `department_id` | BIGINT | FOREIGN KEY → departments(id), NOT NULL | Department |
| `is_primary` | BOOLEAN | DEFAULT FALSE | Primary department flag |

---

### 13. **notifications**
User notifications.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Notification identifier |
| `user_id` | BIGINT | FOREIGN KEY → users(id), NOT NULL | Recipient |
| `title` | VARCHAR(200) | NOT NULL | Notification title |
| `message` | TEXT | NOT NULL | Notification content |
| `type` | VARCHAR(50) | NOT NULL | Notification type |
| `is_read` | BOOLEAN | DEFAULT FALSE | Read status |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation time |

---

### 14. **activity_log**
User activity tracking.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Log identifier |
| `user_id` | BIGINT | FOREIGN KEY → users(id), NOT NULL | User |
| `action` | VARCHAR(100) | NOT NULL | Action performed |
| `entity_type` | VARCHAR(50) | NOT NULL | Entity affected |
| `entity_id` | BIGINT | NOT NULL | Entity identifier |
| `ip_address` | VARCHAR(50) | NULL | User IP address |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Action time |

---

### 15. **audit_log**
System audit trail for compliance.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Audit identifier |
| `user_id` | BIGINT | FOREIGN KEY → users(id), NULL | User (if applicable) |
| `action` | VARCHAR(100) | NOT NULL | Action performed |
| `table_name` | VARCHAR(100) | NOT NULL | Table affected |
| `record_id` | BIGINT | NOT NULL | Record identifier |
| `old_values` | JSON | NULL | Previous values |
| `new_values` | JSON | NULL | New values |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Audit time |

---

## 🔄 Workflow Tables

### 16. **workflows**
Workflow definitions.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Workflow identifier |
| `name` | VARCHAR(100) | NOT NULL | Workflow name |
| `description` | TEXT | NULL | Workflow description |
| `category_id` | BIGINT | FOREIGN KEY → service_categories(id), NULL | Applicable category |
| `is_active` | BOOLEAN | DEFAULT TRUE | Workflow status |

---

### 17. **workflow_steps**
Steps within workflows.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Step identifier |
| `workflow_id` | BIGINT | FOREIGN KEY → workflows(id), NOT NULL | Parent workflow |
| `step_order` | INT | NOT NULL | Step sequence |
| `step_name` | VARCHAR(100) | NOT NULL | Step name |
| `step_type` | VARCHAR(50) | NOT NULL | Step type |
| `configuration` | JSON | NULL | Step configuration |

---

## 📊 Database Initialization Scripts

### Create Database
```sql
CREATE DATABASE IF NOT EXISTS service_request_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE service_request_db;
```

### Initialize Roles
```sql
INSERT INTO roles (name, description) VALUES
('ROLE_USER', 'Standard user with basic access'),
('ROLE_ADMIN', 'Administrator with full system access'),
('ROLE_DEPARTMENT', 'Department user for handling assigned requests'),
('ROLE_AGENT', 'Service agent for processing requests'),
('ROLE_APPROVER', 'Approver for request approvals');
```

### Create Admin User
```sql
-- Password: admin123 (BCrypt encrypted)
INSERT INTO users (username, email, password, first_name, last_name, is_active)
VALUES ('admin', 'admin@example.com', 
'$2a$10$YourBCryptHashHere', 'Admin', 'User', 1);

-- Assign admin role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';
```

---

## 🔍 Useful Queries

### Get User's Requests
```sql
SELECT r.*, c.name as category_name, rt.name as request_type_name
FROM service_requests r
JOIN service_categories c ON r.category_id = c.id
JOIN request_types rt ON r.request_type_id = rt.id
WHERE r.user_id = ?
ORDER BY r.created_at DESC;
```

### Get Department's Assigned Requests
```sql
SELECT r.*, u.first_name, u.last_name, c.name as category_name
FROM service_requests r
JOIN users u ON r.user_id = u.id
JOIN service_categories c ON r.category_id = c.id
WHERE r.department_id = ?
AND r.status NOT IN ('CLOSED', 'REJECTED')
ORDER BY r.priority DESC, r.created_at ASC;
```

### Get Request Statistics
```sql
SELECT 
    status,
    COUNT(*) as count,
    AVG(TIMESTAMPDIFF(HOUR, created_at, resolved_at)) as avg_resolution_hours
FROM service_requests
WHERE resolved_at IS NOT NULL
GROUP BY status;
```

---

## 🔧 Maintenance

### Backup Database
```bash
mysqldump -u root -p service_request_db > backup_$(date +%Y%m%d).sql
```

### Restore Database
```bash
mysql -u root -p service_request_db < backup_20251229.sql
```

### Optimize Tables
```sql
OPTIMIZE TABLE service_requests;
OPTIMIZE TABLE users;
OPTIMIZE TABLE request_comments;
```

---

**Last Updated:** December 2025
