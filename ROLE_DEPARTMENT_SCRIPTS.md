# Batch Scripts for ROLE_DEPARTMENT Management

This document contains the batch scripts you need. Copy the content and save them as `.bat` files in your project root.

---

## 1. setup-role-department.bat

**Purpose:** Ensures ROLE_DEPARTMENT exists in the database

**File:** `setup-role-department.bat`

```batch
@echo off
REM Setup ROLE_DEPARTMENT in the database
REM This script ensures the ROLE_DEPARTMENT role exists in the roles table

echo ========================================
echo Setting up ROLE_DEPARTMENT
echo ========================================
echo.

REM Database configuration
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=service_desk_db
set DB_USER=root
set DB_PASS=root

echo Connecting to database: %DB_NAME%
echo.

REM Create SQL script
echo Creating ROLE_DEPARTMENT...
mysql -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "INSERT INTO roles (name, description, created_at, updated_at) SELECT 'ROLE_DEPARTMENT', 'Department user role with access to department dashboard', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_DEPARTMENT');"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo SUCCESS: ROLE_DEPARTMENT setup complete
    echo ========================================
    echo.
    echo Verifying role...
    mysql -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "SELECT id, name, description FROM roles WHERE name = 'ROLE_DEPARTMENT';"
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to setup ROLE_DEPARTMENT
    echo ========================================
    echo Please check your database connection and try again.
)

echo.
pause
```

---

## 2. assign-department-role.bat

**Purpose:** Interactive script to assign ROLE_DEPARTMENT to a specific user

**File:** `assign-department-role.bat`

```batch
@echo off
REM Assign ROLE_DEPARTMENT to a user
REM This script assigns the ROLE_DEPARTMENT role to a specified user

echo ========================================
echo Assign ROLE_DEPARTMENT to User
echo ========================================
echo.

REM Get user input
set /p USERNAME="Enter username: "

if "%USERNAME%"=="" (
    echo ERROR: Username cannot be empty
    pause
    exit /b 1
)

echo.
echo Looking up user: %USERNAME%
echo.

REM Database configuration
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=service_desk_db
set DB_USER=root
set DB_PASS=root

REM Get user ID
for /f "tokens=*" %%i in ('mysql -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% -sN -e "SELECT id FROM users WHERE username='%USERNAME%';"') do set USER_ID=%%i

if "%USER_ID%"=="" (
    echo ERROR: User '%USERNAME%' not found
    pause
    exit /b 1
)

echo Found user ID: %USER_ID%
echo.

REM Get role ID for ROLE_DEPARTMENT
for /f "tokens=*" %%i in ('mysql -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% -sN -e "SELECT id FROM roles WHERE name='ROLE_DEPARTMENT';"') do set ROLE_ID=%%i

if "%ROLE_ID%"=="" (
    echo ERROR: ROLE_DEPARTMENT not found in database
    echo Please run setup-role-department.bat first
    pause
    exit /b 1
)

echo Found ROLE_DEPARTMENT ID: %ROLE_ID%
echo.

REM Check if user already has the role
for /f "tokens=*" %%i in ('mysql -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% -sN -e "SELECT COUNT(*) FROM user_roles WHERE user_id=%USER_ID% AND role_id=%ROLE_ID%;"') do set HAS_ROLE=%%i

if "%HAS_ROLE%"=="1" (
    echo User '%USERNAME%' already has ROLE_DEPARTMENT
    echo.
    pause
    exit /b 0
)

REM Assign the role
echo Assigning ROLE_DEPARTMENT to user '%USERNAME%'...
mysql -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "INSERT INTO user_roles (user_id, role_id) VALUES (%USER_ID%, %ROLE_ID%);"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo SUCCESS: Role assigned successfully
    echo ========================================
    echo.
    echo User: %USERNAME%
    echo Role: ROLE_DEPARTMENT
    echo.
    echo Verifying assignment...
    mysql -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "SELECT u.username, r.name as role FROM users u JOIN user_roles ur ON u.id = ur.user_id JOIN roles r ON ur.role_id = r.id WHERE u.username = '%USERNAME%';"
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to assign role
    echo ========================================
)

echo.
pause
```

---

## 3. list-department-users.bat

**Purpose:** List all users with ROLE_DEPARTMENT

**File:** `list-department-users.bat`

```batch
@echo off
REM List all users with ROLE_DEPARTMENT
REM This script displays all users who have the ROLE_DEPARTMENT role

echo ========================================
echo Users with ROLE_DEPARTMENT
echo ========================================
echo.

REM Database configuration
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=service_desk_db
set DB_USER=root
set DB_PASS=root

echo Fetching users...
echo.

mysql -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "SELECT u.id, u.username, u.email, u.first_name, u.last_name, u.department, u.is_active FROM users u JOIN user_roles ur ON u.id = ur.user_id JOIN roles r ON ur.role_id = r.id WHERE r.name = 'ROLE_DEPARTMENT' ORDER BY u.username;"

echo.
pause
```

---

## Usage Instructions

### Step 1: Setup the Role
1. Copy the content of `setup-role-department.bat` above
2. Save it as `setup-role-department.bat` in your project root (`d:\Final year Project\`)
3. Run the script by double-clicking it
4. Verify that ROLE_DEPARTMENT was created successfully

### Step 2: Assign Role to Users
1. Copy the content of `assign-department-role.bat` above
2. Save it as `assign-department-role.bat` in your project root
3. Run the script by double-clicking it
4. Enter the username when prompted
5. Verify the role was assigned successfully

### Step 3: List Department Users (Optional)
1. Copy the content of `list-department-users.bat` above
2. Save it as `list-department-users.bat` in your project root
3. Run the script to see all users with ROLE_DEPARTMENT

---

## Alternative: Direct SQL Commands

If you prefer to run SQL commands directly, use these:

### Create ROLE_DEPARTMENT:
```sql
INSERT INTO roles (name, description, created_at, updated_at) 
SELECT 'ROLE_DEPARTMENT', 'Department user role with access to department dashboard', NOW(), NOW() 
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_DEPARTMENT');
```

### Assign to a user (replace 'username' with actual username):
```sql
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'username' AND r.name = 'ROLE_DEPARTMENT'
AND NOT EXISTS (
    SELECT 1 FROM user_roles ur 
    WHERE ur.user_id = u.id AND ur.role_id = r.id
);
```

### List users with ROLE_DEPARTMENT:
```sql
SELECT u.id, u.username, u.email, u.first_name, u.last_name, u.department, u.is_active
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE r.name = 'ROLE_DEPARTMENT'
ORDER BY u.username;
```
