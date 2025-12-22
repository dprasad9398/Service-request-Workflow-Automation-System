@echo off
echo ========================================
echo Fixing Database Schema
echo ========================================
echo.

cd /d "d:\Final year Project"

echo Applying database fixes...
echo.

REM Check if MySQL is accessible
where mysql >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: MySQL command not found in PATH!
    echo Please ensure MySQL is installed and added to PATH
    pause
    exit /b 1
)

echo Fixing roles table...
mysql -u root -pDurga@123 service_request_db < fix-roles-table.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo SUCCESS: Database schema fixed!
    echo ========================================
    echo.
    echo The roles table now has the required columns.
    echo You can now try logging in again.
    echo.
) else (
    echo.
    echo ERROR: Failed to apply database fixes
    echo Please check the error messages above
    echo.
)

pause
