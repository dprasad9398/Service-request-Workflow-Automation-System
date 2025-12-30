# 🚀 Quick Setup & Deployment Guide

## 📋 Prerequisites Checklist

Before starting, ensure you have:

- ✅ **Java 17 or higher** - [Download](https://www.oracle.com/java/technologies/downloads/)
- ✅ **Node.js 16+ and npm** - [Download](https://nodejs.org/)
- ✅ **MySQL 8.0+** - [Download](https://dev.mysql.com/downloads/mysql/)
- ✅ **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi)
- ✅ **Git** (optional) - [Download](https://git-scm.com/)

### Verify Installations

```bash
# Check Java version
java -version
# Should show: java version "17.x.x" or higher

# Check Node.js and npm
node --version  # Should show: v16.x.x or higher
npm --version   # Should show: 8.x.x or higher

# Check MySQL
mysql --version  # Should show: mysql Ver 8.0.x

# Check Maven
mvn --version   # Should show: Apache Maven 3.6.x or higher
```

---

## 🗄️ Step 1: Database Setup

### 1.1 Start MySQL Server

**Windows:**
```bash
# Start MySQL service
net start MySQL80
```

**Linux/Mac:**
```bash
sudo systemctl start mysql
# or
sudo service mysql start
```

### 1.2 Create Database

```bash
# Login to MySQL
mysql -u root -p
# Enter your MySQL root password
```

```sql
-- Create database
CREATE DATABASE service_request_db;

-- Verify creation
SHOW DATABASES;

-- Exit MySQL
exit;
```

### 1.3 Run Initialization Scripts

**Option A: Using Batch File (Windows)**
```bash
cd "d:\Final year Project"
.\setup-database.bat
```

**Option B: Manual SQL Execution**
```bash
# Navigate to project directory
cd "d:\Final year Project"

# Run initialization scripts
mysql -u root -p service_request_db < init-roles.sql
mysql -u root -p service_request_db < create-demo-users.sql
mysql -u root -p service_request_db < setup-categories-and-types.sql
```

---

## ⚙️ Step 2: Backend Configuration

### 2.1 Configure Database Connection

Edit [`service-request-backend/src/main/resources/application.properties`](file:///d:/Final%20year%20Project/service-request-backend/src/main/resources/application.properties):

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/service_request_db
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

# Change YOUR_MYSQL_PASSWORD to your actual MySQL root password
```

### 2.2 Build Backend

```bash
# Navigate to backend directory
cd "d:\Final year Project\service-request-backend"

# Clean and build
mvn clean install

# This may take 2-3 minutes on first run
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: 2.5 min
```

### 2.3 Start Backend Server

**Option A: Using Batch File (Recommended)**
```bash
cd "d:\Final year Project"
.\start-backend.bat
```

**Option B: Using Maven**
```bash
cd "d:\Final year Project\service-request-backend"
mvn spring-boot:run
```

**Success Indicators:**
```
Started ServiceRequestApplication in X.XXX seconds
Tomcat started on port(s): 8080 (http)
```

**Backend URL:** `http://localhost:8080`

---

## 🎨 Step 3: Frontend Setup

### 3.1 Install Dependencies

```bash
# Navigate to frontend directory
cd "d:\Final year Project\service-request-frontend"

# Install npm packages
npm install

# This may take 3-5 minutes on first run
```

### 3.2 Configure API Endpoint (Optional)

If your backend is not on `localhost:8080`, edit [`service-request-frontend/src/api/axios.js`](file:///d:/Final%20year%20Project/service-request-frontend/src/api/axios.js):

```javascript
const api = axios.create({
  baseURL: 'http://localhost:8080/api',  // Change if needed
  // ...
});
```

### 3.3 Start Frontend Server

**Option A: Using Batch File (Recommended)**
```bash
cd "d:\Final year Project"
.\start-frontend.bat
```

**Option B: Using npm**
```bash
cd "d:\Final year Project\service-request-frontend"
npm run dev
```

**Success Indicators:**
```
VITE v4.x.x ready in XXX ms
➜  Local:   http://localhost:3000/
➜  Network: use --host to expose
```

**Frontend URL:** `http://localhost:3000`

---

## 👤 Step 4: Create Admin User

### Option A: Using Batch File (Recommended)

```bash
cd "d:\Final year Project"
.\create-admin-user.bat
```

### Option B: Using SQL

```bash
mysql -u root -p service_request_db
```

```sql
-- Insert admin user (password: admin123)
INSERT INTO users (username, email, password, first_name, last_name, is_active)
VALUES ('admin', 'admin@example.com', 
'$2a$10$N9qo8uLOickgx2ZMRZoMye1J8YJZ9Q8NQ6sV5K5ryDAhwVZLKxPW6', 
'Admin', 'User', 1);

-- Assign admin role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';

-- Verify
SELECT u.username, u.email, r.name as role
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE u.username = 'admin';
```

---

## ✅ Step 5: Verify Installation

### 5.1 Check Backend Health

Open browser and navigate to:
```
http://localhost:8080/api/auth/login
```

You should see a response (even if it's an error, it means the backend is running).

### 5.2 Access Frontend

Open browser and navigate to:
```
http://localhost:3000
```

You should see the login page.

### 5.3 Test Login

**Admin Credentials:**
- **Username:** `admin`
- **Password:** `admin123`

**Expected Result:** Redirect to Admin Dashboard

---

## 🔧 Common Setup Issues & Solutions

### Issue 1: Backend Won't Start - Port 8080 Already in Use

**Solution:**
```bash
# Windows: Find and kill process on port 8080
netstat -ano | findstr :8080
taskkill /PID <PID_NUMBER> /F

# Linux/Mac:
lsof -i :8080
kill -9 <PID_NUMBER>
```

### Issue 2: Database Connection Failed

**Symptoms:**
```
Communications link failure
Access denied for user 'root'@'localhost'
```

**Solutions:**
1. Verify MySQL is running:
   ```bash
   # Windows
   net start MySQL80
   
   # Linux/Mac
   sudo systemctl status mysql
   ```

2. Check credentials in `application.properties`

3. Test connection:
   ```bash
   cd "d:\Final year Project"
   .\test-database-connection.bat
   ```

### Issue 3: Frontend Can't Connect to Backend

**Symptoms:**
- Network errors in browser console
- 404 errors on API calls

**Solutions:**
1. Verify backend is running on port 8080
2. Check CORS configuration in backend
3. Clear browser cache and reload

### Issue 4: Maven Build Fails

**Symptoms:**
```
[ERROR] Failed to execute goal
[ERROR] BUILD FAILURE
```

**Solutions:**
1. Clean Maven cache:
   ```bash
   mvn clean
   mvn dependency:purge-local-repository
   ```

2. Update Maven dependencies:
   ```bash
   mvn clean install -U
   ```

3. Check Java version:
   ```bash
   java -version  # Must be 17+
   ```

### Issue 5: npm install Fails

**Solutions:**
1. Clear npm cache:
   ```bash
   npm cache clean --force
   ```

2. Delete `node_modules` and retry:
   ```bash
   rm -rf node_modules package-lock.json
   npm install
   ```

3. Use legacy peer deps:
   ```bash
   npm install --legacy-peer-deps
   ```

---

## 🎯 Quick Start Commands Reference

### Daily Development Workflow

**1. Start Backend:**
```bash
cd "d:\Final year Project"
.\start-backend.bat
# Wait for "Started ServiceRequestApplication" message
```

**2. Start Frontend:**
```bash
cd "d:\Final year Project"
.\start-frontend.bat
# Wait for "ready in XXX ms" message
```

**3. Access Application:**
- Frontend: `http://localhost:3000`
- Backend API: `http://localhost:8080/api`

### Stop Servers

**Backend:**
- Press `Ctrl + C` in the terminal running the backend

**Frontend:**
- Press `Ctrl + C` in the terminal running the frontend

---

## 🔄 Restart & Rebuild

### Clean Restart Backend

```bash
cd "d:\Final year Project"
.\clean-restart-backend.bat
```

This will:
1. Stop backend
2. Clean Maven build
3. Rebuild
4. Restart

### Restart Frontend

```bash
cd "d:\Final year Project\service-request-frontend"
npm run dev
```

---

## 📊 Initial Data Setup

### Create Sample Departments

```bash
cd "d:\Final year Project"
mysql -u root -p service_request_db
```

```sql
INSERT INTO departments (name, description, email, is_active) VALUES
('IT Support', 'Information Technology support services', 'it@example.com', 1),
('HR Department', 'Human Resources services', 'hr@example.com', 1),
('Facilities', 'Facilities and maintenance services', 'facilities@example.com', 1),
('General Services', 'General administrative services', 'general@example.com', 1);
```

### Create Test Users

```bash
.\create-demo-users.sql
```

This creates:
- **User:** `user1` / Password: `user123` (ROLE_USER)
- **Department User:** `dept1` / Password: `dept123` (ROLE_DEPARTMENT)
- **Admin:** `admin` / Password: `admin123` (ROLE_ADMIN)

---

## 🚀 Production Deployment

### Build for Production

**Backend:**
```bash
cd "d:\Final year Project\service-request-backend"
mvn clean package -DskipTests
# JAR file created in: target/service-request-backend-0.0.1-SNAPSHOT.jar
```

**Frontend:**
```bash
cd "d:\Final year Project\service-request-frontend"
npm run build
# Production files created in: dist/
```

### Run Production Backend

```bash
java -jar target/service-request-backend-0.0.1-SNAPSHOT.jar
```

### Serve Production Frontend

Use a web server like Nginx or Apache to serve the `dist/` folder.

---

## 📝 Environment Variables

### Backend Environment Variables

Create `.env` file in backend root (optional):
```properties
DB_URL=jdbc:mysql://localhost:3306/service_request_db
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000
```

### Frontend Environment Variables

Create `.env` file in frontend root:
```properties
VITE_API_URL=http://localhost:8080/api
```

---

## 🔒 Security Checklist

Before deploying to production:

- [ ] Change default admin password
- [ ] Update JWT secret key
- [ ] Enable HTTPS
- [ ] Configure firewall rules
- [ ] Set up database backups
- [ ] Enable audit logging
- [ ] Review CORS settings
- [ ] Update default passwords in SQL scripts

---

## 📞 Getting Help

If you encounter issues:

1. **Check Logs:**
   - Backend: Console output or `logs/` directory
   - Frontend: Browser console (F12)

2. **Review Documentation:**
   - [`PROJECT_OVERVIEW.md`](file:///d:/Final%20year%20Project/PROJECT_OVERVIEW.md)
   - [`BACKEND_ARCHITECTURE.md`](file:///d:/Final%20year%20Project/BACKEND_ARCHITECTURE.md)
   - [`FRONTEND_ARCHITECTURE.md`](file:///d:/Final%20year%20Project/FRONTEND_ARCHITECTURE.md)

3. **Run Diagnostic Scripts:**
   ```bash
   .\test-database-connection.bat
   .\troubleshoot-login.bat
   ```

---

## ✅ Setup Complete!

You should now have:
- ✅ MySQL database running with schema
- ✅ Backend API running on port 8080
- ✅ Frontend application running on port 3000
- ✅ Admin user created and functional
- ✅ Sample data loaded

**Next Steps:**
1. Login as admin (`admin` / `admin123`)
2. Create additional users
3. Configure departments
4. Set up service catalog
5. Start creating requests!

---

**Last Updated:** December 2025
