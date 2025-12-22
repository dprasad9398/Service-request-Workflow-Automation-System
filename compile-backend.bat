@echo off
echo ========================================
echo Cleaning and Compiling Backend
echo ========================================
echo.

cd /d "d:\Final year Project\service-request-backend"

echo Step 1: Cleaning previous build...
call mvn clean
if %errorlevel% neq 0 (
    echo ERROR: Maven clean failed
    pause
    exit /b 1
)

echo.
echo Step 2: Compiling with Java 21 and Lombok 1.18.34...
call mvn compile
if %errorlevel% neq 0 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)

echo.
echo ========================================
echo SUCCESS: Backend compiled successfully!
echo ========================================
echo.
echo Now you can run:
echo   mvn spring-boot:run
echo.
pause
