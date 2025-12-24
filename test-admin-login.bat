@echo off
echo ================================================
echo Testing Admin Login - Backend Authentication
echo ================================================
echo.

echo Testing login with admin credentials...
echo.

REM Create a temporary JSON file for the request
echo {"username":"admin","password":"Admin@123"} > temp_login.json

REM Test the login endpoint
echo Calling POST http://localhost:8080/auth/login
curl -X POST http://localhost:8080/auth/login ^
  -H "Content-Type: application/json" ^
  -d @temp_login.json

echo.
echo.
echo ================================================
echo.

REM Clean up
del temp_login.json

pause
