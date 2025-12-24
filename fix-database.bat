@echo off
echo ========================================
echo Service Catalog Database Fix
echo ========================================
echo.
echo This will add missing columns to service_catalog table
echo.
pause

echo Running SQL fix...
mysql -u root -p service_request_db < "d:\Final year Project\manual-fix.sql"

echo.
echo ========================================
echo Done! Now restart your Spring Boot backend
echo ========================================
pause
