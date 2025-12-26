# Service Request Workflow Automation System

## Project Overview
A comprehensive service desk management system built with Spring Boot (backend) and React (frontend) for managing service requests, categories, and workflows.

## Features
- ✅ User Authentication & Authorization (JWT)
- ✅ Role-based Access Control (Admin, User, Agent, Approver)
- ✅ Service Request Management (Create, View, Update, Delete)
- ✅ Category & Request Type Management
- ✅ Admin Dashboard with Statistics
- ✅ User Management (CRUD)
- ✅ Service Catalog Management
- ✅ Request Status Tracking
- ✅ Priority Management
- ✅ Department Assignment
- ✅ Request Escalation

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.x
- Spring Security (JWT Authentication)
- Spring Data JPA
- MySQL Database
- Maven

### Frontend
- React 18
- Material-UI (MUI)
- Axios
- React Router
- Context API for state management

## Project Structure
```
service-request-system/
├── service-request-backend/     # Spring Boot backend
│   ├── src/main/java/
│   │   └── com/servicedesk/
│   │       ├── controller/      # REST controllers
│   │       ├── service/         # Business logic
│   │       ├── entity/          # JPA entities
│   │       ├── repository/      # Data repositories
│   │       ├── dto/             # Data transfer objects
│   │       ├── config/          # Configuration classes
│   │       └── util/            # Utility classes
│   └── src/main/resources/
│       └── application.properties
│
└── service-request-frontend/    # React frontend
    ├── src/
    │   ├── components/          # Reusable components
    │   ├── pages/               # Page components
    │   ├── services/            # API services
    │   ├── context/             # Context providers
    │   └── api/                 # API configuration
    └── public/
```

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Node.js 16+ and npm
- MySQL 8.0+
- Maven 3.6+

### Database Setup
1. Create MySQL database:
```sql
CREATE DATABASE service_request_db;
```

2. Update database credentials in `service-request-backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/service_request_db
spring.datasource.username=root
spring.datasource.password=your_password
```

3. Run database migrations (optional - tables auto-created by JPA):
```bash
cd "d:\Final year Project"
.\run-category-migration.bat
.\run-setup-categories.bat
```

### Backend Setup
1. Navigate to backend directory:
```bash
cd service-request-backend
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

Backend will start on `http://localhost:8080`

### Frontend Setup
1. Navigate to frontend directory:
```bash
cd service-request-frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

Frontend will start on `http://localhost:3000`

## Default Users
After initial setup, you can create users through the registration page or use SQL:

```sql
-- Admin user (password: admin123)
INSERT INTO users (username, email, password, first_name, last_name, is_active) 
VALUES ('admin', 'admin@example.com', '$2a$10$...', 'Admin', 'User', 1);
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `POST /api/auth/refresh` - Refresh token

### Requests (User)
- `GET /api/requests/my-requests` - Get user's requests
- `POST /api/requests` - Create new request
- `GET /api/requests/{id}` - Get request details

### Admin
- `GET /api/admin/requests` - Get all requests
- `PUT /api/admin/requests/{id}/status` - Update request status
- `PATCH /api/admin/requests/{id}/priority` - Update priority
- `DELETE /api/admin/requests/{id}` - Delete request
- `GET /api/admin/users` - Manage users
- `GET /api/service-categories` - Manage categories

## Features in Detail

### Request Management
- Create requests by selecting category and request type
- Track request status (NEW, IN_PROGRESS, RESOLVED, CLOSED)
- Set priority levels (LOW, MEDIUM, HIGH, CRITICAL)
- View request history and timeline

### Category System
- 4 main categories: IT Support, Facilities, HR Request, General
- 27+ request types across categories
- Dynamic category-to-department mapping

### Admin Capabilities
- User management (create, edit, delete, activate/deactivate)
- Service catalog configuration
- Category and request type management
- Request monitoring and management
- Status and priority updates
- Request escalation
- Request deletion

## Recent Fixes & Improvements
- ✅ Fixed missing request types for Facilities and General categories
- ✅ Added 13 HR-specific request types
- ✅ Fixed category-based request creation flow
- ✅ Resolved null pointer exceptions in workflow triggers
- ✅ Fixed service_id nullable constraint for category-based requests
- ✅ Added admin delete functionality with confirmation
- ✅ Fixed duplicate Facilities category issue

## Known Issues
- Dashboard statistics currently show placeholder data (needs connection to real data)
- Reports & Analytics page not yet implemented
- Department management UI not yet implemented
- System settings page not yet implemented

## Future Enhancements
- Reports and analytics dashboard
- Department management interface
- Bulk request operations
- System settings configuration
- Audit log viewer
- Email notifications
- SLA tracking and alerts
- Advanced search and filters

## Contributing
This is a final year project. For any questions or issues, please contact the development team.

## License
This project is developed as part of academic requirements.

## Contact
For support or queries, please reach out to the project maintainers.
