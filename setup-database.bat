@echo off
echo ========================================
echo Service Request System - Setup Script
echo ========================================
echo.

REM Check MySQL
echo [1/6] Checking MySQL...
mysql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: MySQL not found in PATH!
    echo Please restart your terminal/IDE after adding MySQL to PATH
    echo MySQL is usually in: C:\Program Files\MySQL\MySQL Server 8.0\bin
    pause
    exit /b 1
)
echo SUCCESS: MySQL found!
echo.

REM Check Maven
echo [2/6] Checking Maven...
mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven not found in PATH!
    echo Please restart your terminal/IDE after adding Maven to PATH
    pause
    exit /b 1
)
echo SUCCESS: Maven found!
echo.

REM Create Database
echo [3/6] Creating database...
echo Please enter your MySQL root password when prompted:
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS service_request_db;"
if %errorlevel% neq 0 (
    echo ERROR: Failed to create database
    pause
    exit /b 1
)
echo SUCCESS: Database created!
echo.

REM Load Schema
echo [4/6] Loading database schema...
cd service-request-backend
mysql -u root -p service_request_db < database-schema.sql
if %errorlevel% neq 0 (
    echo ERROR: Failed to load schema
    pause
    exit /b 1
)
echo SUCCESS: Schema loaded!
echo.

REM Verify Tables
echo [5/6] Verifying tables...
mysql -u root -p service_request_db -e "SHOW TABLES;"
echo.

echo [6/6] Setup complete!
echo.
echo ========================================
echo Next Steps:
echo ========================================
echo 1. Update MySQL password in:
echo    service-request-backend\src\main\resources\application.properties
echo    (Line 3: spring.datasource.password=YOUR_PASSWORD)
echo.
echo 2. Run backend:
echo    cd service-request-backend
echo    mvn spring-boot:run
echo.
echo 3. Run frontend (in new terminal):
echo    cd service-request-frontend
echo    npm install
echo    npm run dev
echo.
echo 4. Open browser:
echo    http://localhost:3000
echo    Login: admin / Admin@123
echo ========================================
pause
