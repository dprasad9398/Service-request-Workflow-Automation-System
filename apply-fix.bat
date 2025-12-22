@echo off
echo Applying database schema fix...
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -pDurga@123 service_request_db < add-columns.sql
if %errorlevel% equ 0 (
    echo SUCCESS: Columns added to roles table!
) else (
    echo ERROR: Failed to add columns
)
pause
