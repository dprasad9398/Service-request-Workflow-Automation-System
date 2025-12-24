@echo off
echo ================================================
echo Admin Request Management - Database Setup
echo ================================================
echo.

echo Running database migration...
mysql -u root -pDurga@123 service_request_db < database-migrations\create-admin-request-management.sql

if %errorlevel% equ 0 (
    echo.
    echo ================================================
    echo Database Setup Complete!
    echo ================================================
    echo.
    echo Departments and mappings created successfully.
    echo.
) else (
    echo.
    echo ERROR: Database migration failed
    echo Please check the error messages above.
    echo.
)

pause
