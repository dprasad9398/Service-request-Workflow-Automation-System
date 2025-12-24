@echo off
echo ================================================
echo Clean Backend Rebuild and Restart
echo ================================================
echo.

cd service-request-backend

echo Step 1: Clean build...
call mvnw clean

echo.
echo Step 2: Compile...
call mvnw compile

echo.
echo Step 3: Package...
call mvnw package -DskipTests

echo.
echo Step 4: Starting application...
call mvnw spring-boot:run

pause
