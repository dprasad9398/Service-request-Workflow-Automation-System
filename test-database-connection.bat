@echo off
echo ========================================
echo Testing Database Connection
echo ========================================
echo.

echo Checking MySQL connection...
echo Please enter your MySQL root password when prompted:
echo.

mysql -u root -p -e "USE service_request_db; SELECT 'Database connection successful!' as Status; SELECT COUNT(*) as TableCount FROM information_schema.tables WHERE table_schema = 'service_request_db';"

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo SUCCESS: Database is accessible!
    echo ========================================
    echo.
    echo Next step: Make sure password is set in application.properties
    echo File: service-request-backend\src\main\resources\application.properties
    echo Line 13: spring.datasource.password=YOUR_PASSWORD
    echo.
) else (
    echo.
    echo ========================================
    echo ERROR: Cannot connect to database
    echo ========================================
    echo.
    echo Please check:
    echo 1. MySQL is running
    echo 2. Database 'service_request_db' exists
    echo 3. Password is correct
    echo.
)

pause
