@echo off
echo ========================================
echo Starting Backend Server
echo ========================================
echo.
echo Database: service_request_db
echo Username: root
echo Password: Durga@123
echo.
echo Starting Spring Boot application...
echo This may take 30-60 seconds on first run...
echo.
echo Backend will be available at: http://localhost:8080/api
echo.
echo Press Ctrl+C to stop the server
echo ========================================
echo.

cd /d "d:\Final year Project\service-request-backend"

REM Check if Maven is available
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Maven not found in PATH!
    echo.
    echo Please do ONE of the following:
    echo.
    echo Option 1: Restart your terminal/IDE
    echo   - Close this window
    echo   - Open new Command Prompt
    echo   - Run this script again
    echo.
    echo Option 2: Use full Maven path
    echo   - Find where Maven is installed
    echo   - Example: "C:\Program Files\Apache\maven\bin\mvn" spring-boot:run
    echo.
    pause
    exit /b 1
)

mvn spring-boot:run
