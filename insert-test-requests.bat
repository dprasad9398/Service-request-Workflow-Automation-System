@echo off
echo ================================================
echo Inserting Test Service Requests
echo ================================================
echo.

mysql -u root -pDurga@123 service_request_db < database-migrations\insert-test-requests.sql

if %errorlevel% equ 0 (
    echo.
    echo ================================================
    echo Test requests inserted successfully!
    echo ================================================
    echo.
    echo Verifying...
    mysql -u root -pDurga@123 service_request_db -e "SELECT COUNT(*) as total FROM service_requests;"
    echo.
) else (
    echo.
    echo ERROR: Failed to insert test requests
    echo.
)

pause
