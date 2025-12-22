@echo off
echo ========================================
echo Troubleshooting Login Issue
echo ========================================
echo.

echo Step 1: Checking if backend is running...
echo.
curl -s http://localhost:8080/api/auth/login >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Backend is running on port 8080
) else (
    echo [ERROR] Backend is NOT running!
    echo.
    echo Please start the backend first:
    echo   cd "d:\Final year Project\service-request-backend"
    echo   mvn spring-boot:run
    echo.
    pause
    exit /b 1
)
echo.

echo Step 2: Checking database connection...
echo Please enter your MySQL password:
mysql -u root -p service_request_db -e "SELECT 'Database connected!' as Status;" 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Cannot connect to database
    pause
    exit /b 1
)
echo.

echo Step 3: Checking if admin user exists...
mysql -u root -p service_request_db -e "SELECT username, email, is_active FROM users WHERE username='admin';"
echo.

echo Step 4: Checking user roles...
mysql -u root -p service_request_db -e "SELECT u.username, r.name as role FROM users u JOIN user_roles ur ON u.id = ur.user_id JOIN roles r ON ur.role_id = r.id WHERE u.username='admin';"
echo.

echo ========================================
echo Diagnosis Complete
echo ========================================
echo.
echo If admin user is missing, run:
echo   mysql -u root -p service_request_db < database-schema.sql
echo.
echo If password is wrong, the hash in database might be incorrect.
echo Expected password: Admin@123
echo.
pause
