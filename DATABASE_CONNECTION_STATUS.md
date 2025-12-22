# 🔌 Database Connection Status

## Current Configuration

Your backend is configured to connect to:

**Database Details** (from `application.properties`):
```properties
URL: jdbc:mysql://localhost:3306/service_request_db
Username: root
Password: (EMPTY - NEEDS TO BE SET!)
```

## ⚠️ Connection Status: NOT CONNECTED YET

**Why?** The password is empty on line 13 of `application.properties`

---

## ✅ What You Have Done

1. ✅ Created database: `service_request_db`
2. ✅ Created all 17 tables in MySQL Workbench
3. ✅ Backend code is ready
4. ❌ **Missing**: Password in `application.properties`

---

## 🔧 How to Connect Database to Backend

### Step 1: Add MySQL Password

**File**: `service-request-backend\src\main\resources\application.properties`

**Line 13** - Change from:
```properties
spring.datasource.password=
```

**To**:
```properties
spring.datasource.password=your_mysql_root_password
```

### Step 2: Test Connection (Optional)

Run this script to verify database is accessible:
```bash
cd "d:\Final year Project"
test-database-connection.bat
```

### Step 3: Start Backend

Once password is added:
```bash
cd "d:\Final year Project\service-request-backend"
mvn spring-boot:run
```

**If connection is successful**, you'll see:
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
Hibernate: select ... from users
Started ServiceDeskApplication in X seconds
```

**If connection fails**, you'll see:
```
Access denied for user 'root'@'localhost'
```

---

## 🧪 Quick Test Commands

### Test 1: Check Database Exists
```bash
mysql -u root -p -e "SHOW DATABASES LIKE 'service_request_db';"
```

### Test 2: Check Tables
```bash
mysql -u root -p service_request_db -e "SHOW TABLES;"
```

### Test 3: Check Admin User
```bash
mysql -u root -p service_request_db -e "SELECT username, email FROM users WHERE username='admin';"
```

---

## 📊 Database Connection Flow

```
Backend (Spring Boot)
    ↓
application.properties (needs password!)
    ↓
MySQL Driver (com.mysql.cj.jdbc.Driver)
    ↓
MySQL Server (localhost:3306)
    ↓
Database: service_request_db
    ↓
Tables: users, roles, service_request, etc.
```

---

## ✅ Connection Checklist

Before starting backend, verify:

- [ ] MySQL is running
- [ ] Database `service_request_db` exists
- [ ] All 17 tables are created
- [ ] Password is set in `application.properties` (line 13)
- [ ] Username is `root` (line 12)
- [ ] URL points to `localhost:3306` (line 11)

---

## 🎯 Next Steps

1. **Add password** to `application.properties` line 13
2. **Save the file**
3. **Start backend**: `mvn spring-boot:run`
4. **Watch console** for connection success
5. **Start frontend**: `npm run dev`
6. **Test login**: http://localhost:3000

---

## 💡 How to Know Connection is Working

**Good Signs**:
- ✅ Backend starts without errors
- ✅ Console shows: "HikariPool-1 - Start completed"
- ✅ Console shows: "Hibernate: select ..."
- ✅ No "Access denied" errors
- ✅ Can login at http://localhost:3000

**Bad Signs**:
- ❌ "Access denied for user 'root'"
- ❌ "Unknown database 'service_request_db'"
- ❌ "Communications link failure"
- ❌ Backend crashes on startup

---

**Current Status**: Database is ready, just needs password in config file!

**Action Required**: Add your MySQL password to line 13 of `application.properties`
