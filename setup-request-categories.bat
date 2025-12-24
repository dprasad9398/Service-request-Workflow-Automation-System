@echo off
echo ================================================
echo Category-Based Request System - Database Setup
echo ================================================
echo.

echo Running database migration...
mysql -u root -pDurga@123 service_request_db < database-migrations\create-request-categories.sql

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Database migration failed
    pause
    exit /b 1
)

echo.
echo ================================================
echo Database Setup Complete!
echo ================================================
echo.
echo Categories created:
echo - IT Support (4 types)
echo - HR Requests (4 types)
echo - Facilities (4 types)
echo - General (3 types)
echo.
echo Total: 4 categories, 15 request types
echo.
pause
