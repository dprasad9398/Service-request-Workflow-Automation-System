@echo off
echo ================================================
echo Creating Admin User via Backend API
echo ================================================
echo.

echo Step 1: Make sure backend is running on port 8080...
timeout /t 2 /nobreak >nul

echo Step 2: Registering admin user via API...
curl -X POST http://localhost:8080/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"email\":\"admin@servicedesk.com\",\"password\":\"Admin@123\",\"firstName\":\"System\",\"lastName\":\"Administrator\",\"phone\":\"1234567890\",\"department\":\"IT\"}"

echo.
echo.
echo Step 3: Assigning ROLE_ADMIN to the user...
mysql -u root -pDurga@123 service_request_db -e "INSERT IGNORE INTO user_roles (user_id, role_id) SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';"

echo.
echo Step 4: Verifying admin user...
mysql -u root -pDurga@123 service_request_db -e "SELECT u.username, u.email, u.is_active, r.name as role FROM users u JOIN user_roles ur ON u.id = ur.user_id JOIN roles r ON ur.role_id = r.id WHERE u.username = 'admin';"

echo.
echo ================================================
echo Setup Complete!
echo ================================================
echo.
echo Login Credentials:
echo   Username: admin
echo   Password: Admin@123
echo.
echo Go to: http://localhost:3000/login
echo Click "Admin Login" tab and use the above credentials
echo.
pause
