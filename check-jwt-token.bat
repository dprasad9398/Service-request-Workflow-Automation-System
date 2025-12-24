@echo off
echo ================================================
echo JWT Token Diagnostic
echo ================================================
echo.
echo Open your browser console (F12) and run:
echo.
echo localStorage.getItem('token')
echo.
echo If you see a long string starting with "eyJ...", the token exists.
echo If you see "null", you need to login again.
echo.
echo ================================================
echo Backend Restart Instructions
echo ================================================
echo.
echo 1. Stop the current backend (Ctrl+C in backend terminal)
echo 2. Run: .\start-backend.bat
echo 3. Wait for "Started ServiceDeskApplication"
echo 4. Refresh browser and login again
echo.
pause
