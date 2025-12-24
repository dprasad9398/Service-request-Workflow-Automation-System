@echo off
echo ================================================
echo Role-Based Authorization System - Setup Script
echo ================================================
echo.

echo Step 1: Initializing database roles...
echo Running init-roles.sql...
mysql -u root -pDurga@123 service_request_db < init-roles.sql

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to initialize roles in database
    echo Please check your MySQL connection and try again
    pause
    exit /b 1
)

echo.
echo ================================================
echo Setup Complete!
echo ================================================
echo.
echo Next steps:
echo 1. Create admin and user test accounts
echo 2. Start the backend: start-backend.bat
echo 3. Start the frontend: start-frontend.bat
echo 4. Test the application at http://localhost:3000
echo.
echo See walkthrough.md for detailed testing instructions
echo.
pause
