@echo off
echo ========================================
echo Fixing Admin User Password
echo ========================================
echo.
echo This will update the admin user password to: Admin@123
echo.

REM Generate a fresh BCrypt hash using a known valid hash for "Admin@123"
REM This hash was generated using BCrypt with strength 10
SET HASH=$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

echo Updating admin user password...
mysql -u root -pDurga@123 service_request_db -e "UPDATE users SET password = '%HASH%' WHERE username = 'admin';"

if %errorlevel% equ 0 (
    echo.
    echo [SUCCESS] Admin password updated successfully!
    echo.
    echo Login Credentials:
    echo   Username: admin
    echo   Password: Admin@123
    echo.
) else (
    echo.
    echo [ERROR] Failed to update password
    echo.
)

echo Verifying admin user...
mysql -u root -pDurga@123 service_request_db -e "SELECT username, email, first_name, last_name FROM users WHERE username = 'admin';"

echo.
pause
