@echo off
echo ================================================
echo Killing processes on port 8080
echo ================================================

FOR /F "tokens=5" %%P IN ('netstat -ano ^| findstr :8080') DO (
    echo Killing process %%P
    taskkill /PID %%P /F
)

echo.
echo Port 8080 is now free
echo.
echo Starting backend...
cd service-request-backend
call mvnw spring-boot:run

pause
