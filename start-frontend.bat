@echo off
echo ========================================
echo Starting Frontend Development Server
echo ========================================
echo.

cd service-request-frontend

echo Installing dependencies (first time only)...
if not exist "node_modules" (
    echo Running npm install...
    call npm install
    if %errorlevel% neq 0 (
        echo ERROR: npm install failed
        pause
        exit /b 1
    )
)

echo.
echo Starting Vite development server...
echo Frontend will be available at: http://localhost:3000
echo.
echo Press Ctrl+C to stop the server
echo.

npm run dev
