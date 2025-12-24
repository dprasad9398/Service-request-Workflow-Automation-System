@echo off
echo ================================================
echo Creating Admin User for Login
echo ================================================
echo.

REM Step 1: Delete existing admin user if exists
echo Step 1: Removing existing admin user...
mysql -u root -pDurga@123 service_request_db -e "DELETE FROM user_roles WHERE user_id = (SELECT id FROM users WHERE username = 'admin');"
mysql -u root -pDurga@123 service_request_db -e "DELETE FROM users WHERE username = 'admin';"

REM Step 2: Create admin user with BCrypt password for "Admin@123"
echo Step 2: Creating new admin user...
mysql -u root -pDurga@123 service_request_db -e "INSERT INTO users (username, email, password, first_name, last_name, phone, department, is_active, created_at, updated_at) VALUES ('admin', 'admin@servicedesk.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System', 'Administrator', '1234567890', 'IT', 1, NOW(), NOW());"

REM Step 3: Assign ROLE_ADMIN
echo Step 3: Assigning ROLE_ADMIN...
mysql -u root -pDurga@123 service_request_db -e "INSERT INTO user_roles (user_id, role_id) SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';"

REM Step 4: Verify
echo.
echo Step 4: Verifying admin user...
mysql -u root -pDurga@123 service_request_db -e "SELECT u.id, u.username, u.email, u.is_active, r.name as role FROM users u JOIN user_roles ur ON u.id = ur.user_id JOIN roles r ON ur.role_id = r.id WHERE u.username = 'admin';"

echo.
echo ================================================
echo Admin User Created Successfully!
echo ================================================
echo.
echo Login Credentials:
echo   Username: admin
echo   Password: Admin@123
echo.
echo You can now login at: http://localhost:3000/login
echo Click on "Admin Login" tab and use the above credentials
echo.
pause
