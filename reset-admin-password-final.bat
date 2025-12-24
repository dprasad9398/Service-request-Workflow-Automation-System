@echo off
echo ================================================
echo Resetting Admin Password Using Backend Encoder
echo ================================================
echo.

echo This will use the backend's own password encoder to set the password.
echo.

echo Step 1: Calling reset endpoint...
curl -X POST http://localhost:8080/api/debug/reset-admin-password ^
  -H "Content-Type: application/json" ^
  -d "{\"password\":\"Admin@123\"}"

echo.
echo.
echo Step 2: Testing if password works...
curl -X POST http://localhost:8080/api/debug/test-password ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"Admin@123\"}"

echo.
echo.
echo ================================================
echo Done! Now try logging in at http://localhost:3000/login
echo Username: admin
echo Password: Admin@123
echo ================================================
echo.
pause
