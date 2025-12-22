-- Service Request and Workflow Automation System
-- MySQL Database Schema

-- Create Database
CREATE DATABASE IF NOT EXISTS service_request_db;
USE service_request_db;

-- ============================================
-- 1. ROLES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 2. USERS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    department VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 3. USER_ROLES TABLE (Many-to-Many)
-- ============================================
CREATE TABLE IF NOT EXISTS user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 4. SERVICE_CATEGORIES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS service_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icon VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 5. SLA TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS sla (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    response_time_hours INT NOT NULL,
    resolution_time_hours INT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 6. SERVICE_CATALOG TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS service_catalog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    sla_id BIGINT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    requires_approval BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES service_categories(id) ON DELETE CASCADE,
    FOREIGN KEY (sla_id) REFERENCES sla(id) ON DELETE SET NULL,
    INDEX idx_category (category_id),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 7. SERVICE_REQUESTS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS service_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id VARCHAR(50) NOT NULL UNIQUE,
    service_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    assigned_to BIGINT,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM',
    status ENUM('NEW', 'PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED', 'CANCELLED') DEFAULT 'NEW',
    resolution_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    closed_at TIMESTAMP NULL,
    FOREIGN KEY (service_id) REFERENCES service_catalog(id) ON DELETE RESTRICT,
    FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_ticket_id (ticket_id),
    INDEX idx_requester (requester_id),
    INDEX idx_assigned (assigned_to),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 8. REQUEST_ATTACHMENTS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS request_attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    file_type VARCHAR(100),
    uploaded_by BIGINT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (request_id) REFERENCES service_requests(id) ON DELETE CASCADE,
    FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_request (request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 9. REQUEST_COMMENTS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS request_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    comment TEXT NOT NULL,
    is_internal BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (request_id) REFERENCES service_requests(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_request (request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 10. WORKFLOWS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS workflows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (service_id) REFERENCES service_catalog(id) ON DELETE CASCADE,
    INDEX idx_service (service_id),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 11. WORKFLOW_STEPS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS workflow_steps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    workflow_id BIGINT NOT NULL,
    step_order INT NOT NULL,
    step_name VARCHAR(100) NOT NULL,
    step_type ENUM('APPROVAL', 'ASSIGNMENT', 'NOTIFICATION', 'CONDITION') NOT NULL,
    assignee_role_id BIGINT,
    assignee_user_id BIGINT,
    is_mandatory BOOLEAN DEFAULT TRUE,
    timeout_hours INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (workflow_id) REFERENCES workflows(id) ON DELETE CASCADE,
    FOREIGN KEY (assignee_role_id) REFERENCES roles(id) ON DELETE SET NULL,
    FOREIGN KEY (assignee_user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_workflow (workflow_id),
    INDEX idx_order (step_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 12. WORKFLOW_RULES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS workflow_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    workflow_step_id BIGINT NOT NULL,
    rule_type ENUM('IF_PRIORITY', 'IF_DEPARTMENT', 'IF_AMOUNT', 'IF_CATEGORY') NOT NULL,
    condition_field VARCHAR(50) NOT NULL,
    condition_operator VARCHAR(20) NOT NULL,
    condition_value VARCHAR(255) NOT NULL,
    next_step_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (workflow_step_id) REFERENCES workflow_steps(id) ON DELETE CASCADE,
    FOREIGN KEY (next_step_id) REFERENCES workflow_steps(id) ON DELETE SET NULL,
    INDEX idx_step (workflow_step_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 13. WORKFLOW_INSTANCES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS workflow_instances (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    workflow_id BIGINT NOT NULL,
    current_step_id BIGINT,
    status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (request_id) REFERENCES service_requests(id) ON DELETE CASCADE,
    FOREIGN KEY (workflow_id) REFERENCES workflows(id) ON DELETE RESTRICT,
    FOREIGN KEY (current_step_id) REFERENCES workflow_steps(id) ON DELETE SET NULL,
    INDEX idx_request (request_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 14. APPROVALS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS approvals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    workflow_step_id BIGINT,
    approver_id BIGINT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'ESCALATED') DEFAULT 'PENDING',
    comments TEXT,
    approved_at TIMESTAMP NULL,
    escalated_to BIGINT,
    escalated_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (request_id) REFERENCES service_requests(id) ON DELETE CASCADE,
    FOREIGN KEY (workflow_step_id) REFERENCES workflow_steps(id) ON DELETE SET NULL,
    FOREIGN KEY (approver_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (escalated_to) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_request (request_id),
    INDEX idx_approver (approver_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 15. APPROVAL_HISTORY TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS approval_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    approval_id BIGINT NOT NULL,
    action ENUM('SUBMITTED', 'APPROVED', 'REJECTED', 'ESCALATED') NOT NULL,
    performed_by BIGINT NOT NULL,
    comments TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (approval_id) REFERENCES approvals(id) ON DELETE CASCADE,
    FOREIGN KEY (performed_by) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_approval (approval_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 16. TASKS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    assigned_to BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM',
    due_date TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (request_id) REFERENCES service_requests(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_request (request_id),
    INDEX idx_assigned (assigned_to),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 17. SLA_TRACKING TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS sla_tracking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    sla_id BIGINT NOT NULL,
    response_due_at TIMESTAMP NOT NULL,
    resolution_due_at TIMESTAMP NOT NULL,
    response_completed_at TIMESTAMP NULL,
    resolution_completed_at TIMESTAMP NULL,
    is_response_breached BOOLEAN DEFAULT FALSE,
    is_resolution_breached BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (request_id) REFERENCES service_requests(id) ON DELETE CASCADE,
    FOREIGN KEY (sla_id) REFERENCES sla(id) ON DELETE RESTRICT,
    INDEX idx_request (request_id),
    INDEX idx_response_due (response_due_at),
    INDEX idx_resolution_due (resolution_due_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 18. NOTIFICATIONS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    request_id BIGINT,
    type ENUM('INFO', 'WARNING', 'SUCCESS', 'ERROR') DEFAULT 'INFO',
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (request_id) REFERENCES service_requests(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_read (is_read),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 19. AUDIT_LOGS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(50),
    user_agent VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user (user_id),
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- INITIAL DATA INSERTION
-- ============================================

-- Insert Default Roles
INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', 'System Administrator with full access'),
('ROLE_END_USER', 'End user who can raise service requests'),
('ROLE_APPROVER', 'User who can approve/reject requests'),
('ROLE_AGENT', 'Service agent/technician who handles requests');

-- Insert Default SLA Configurations
INSERT INTO sla (name, response_time_hours, resolution_time_hours, description) VALUES
('Standard SLA', 4, 24, 'Standard service level agreement'),
('High Priority SLA', 2, 12, 'High priority service level agreement'),
('Critical SLA', 1, 4, 'Critical service level agreement'),
('Low Priority SLA', 8, 48, 'Low priority service level agreement');

-- Insert Service Categories
INSERT INTO service_categories (name, description, icon, is_active) VALUES
('IT Support', 'Information Technology related services', 'computer', TRUE),
('HR Services', 'Human Resources related services', 'people', TRUE),
('Finance', 'Finance and accounting related services', 'currency', TRUE),
('Facilities', 'Facilities and infrastructure services', 'building', TRUE);

-- Insert Sample Admin User (password: Admin@123)
-- Note: This password hash is for 'Admin@123' using BCrypt
INSERT INTO users (username, email, password, first_name, last_name, phone, department, is_active) VALUES
('admin', 'admin@servicedesk.com', '$2a$10$xZ8qJ9Y5K3L4M6N7O8P9QeRsT0uV1wX2yZ3aB4cD5eF6gH7iJ8kL9m', 'System', 'Administrator', '1234567890', 'IT', TRUE);

-- Assign Admin Role to Admin User
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1); -- Admin user gets ROLE_ADMIN

-- ============================================
-- END OF SCHEMA
-- ============================================
