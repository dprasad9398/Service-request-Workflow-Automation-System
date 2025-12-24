@echo off
echo ================================================
echo Assign ROLE_ADMIN to User
echo ================================================
echo.

set /p USERNAME="Enter username to make admin (default: admin): "
if "%USERNAME%"=="" set USERNAME=admin

echo.
echo Assigning ROLE_ADMIN to user: %USERNAME%
echo.

mysql -u root -pDurga@123 service_request_db -e "INSERT INTO user_roles (user_id, role_id) SELECT u.id, r.id FROM users u, roles r WHERE u.username = '%USERNAME%' AND r.name = 'ROLE_ADMIN' ON DUPLICATE KEY UPDATE user_id=user_id;"

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to assign role
    pause
    exit /b 1
)

echo.
echo Verifying role assignment...
mysql -u root -pDurga@123 service_request_db -e "SELECT u.username, u.email, r.name as role FROM users u JOIN user_roles ur ON u.id = ur.user_id JOIN roles r ON ur.role_id = r.id WHERE u.username = '%USERNAME%';"

echo.
echo ================================================
echo Success! User %USERNAME% is now an admin
echo ================================================
echo.
echo You can now login with:
echo Username: %USERNAME%
echo Password: (the password you set during registration)
echo.
pause
