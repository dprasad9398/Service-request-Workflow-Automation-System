@echo off
echo ================================================
echo FINAL Admin Password Reset
echo ================================================
echo.

echo Step 1: Delete existing admin user...
mysql -u root -pDurga@123 service_request_db -e "DELETE FROM user_roles WHERE user_id = (SELECT id FROM users WHERE username = 'admin'); DELETE FROM users WHERE username = 'admin';"

echo.
echo Step 2: Create admin user using backend API (ensures correct BCrypt encoding)...
echo Make sure backend is running!
timeout /t 2 /nobreak >nul

curl -X POST http://localhost:8080/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"email\":\"admin@servicedesk.com\",\"password\":\"Admin@123\",\"firstName\":\"System\",\"lastName\":\"Administrator\",\"phone\":\"1234567890\",\"department\":\"IT\"}"

echo.
echo.
echo Step 3: Assign ROLE_ADMIN...
mysql -u root -pDurga@123 service_request_db -e "INSERT INTO user_roles (user_id, role_id) SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';"

echo.
echo Step 4: Verify setup...
mysql -u root -pDurga@123 service_request_db -e "SELECT u.username, u.email, r.name as role FROM users u JOIN user_roles ur ON u.id = ur.user_id JOIN roles r ON ur.role_id = r.id WHERE u.username = 'admin';"

echo.
echo ================================================
echo Admin user created!
echo ================================================
echo.
echo Login at: http://localhost:3000/login
echo Click "Admin Login" tab
echo Username: admin
echo Password: Admin@123
echo.
pause
