# ⚛️ Frontend Architecture Guide

## Overview
The frontend is built using **React 18** with **Material-UI (MUI)** components, following modern React patterns and best practices for scalable single-page applications.

---

## 📐 Architecture Pattern

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                    │
│                   (React Components)                     │
│  • Pages (Route Components)                              │
│  • Reusable Components                                   │
│  • Layout Components                                     │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                   STATE MANAGEMENT                       │
│                  (React Context API)                     │
│  • AuthContext (User authentication state)               │
│  • RequestContext (Request data state)                   │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                    SERVICE LAYER                         │
│                  (API Communication)                     │
│  • authService.js        • requestService.js             │
│  • adminService.js       • departmentService.js          │
│  • userService.js        • categoryService.js            │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                   BACKEND API                            │
│              (Spring Boot REST API)                      │
└─────────────────────────────────────────────────────────┘
```

---

## 📁 Directory Structure

```
src/
│
├── 📄 App.jsx                          # Main application component
├── 📄 main.jsx                         # Application entry point
│
├── 📂 pages/                           # Page Components (Route-level)
│   │
│   ├── 📄 Login.jsx                    # Login page
│   ├── 📄 Register.jsx                 # User registration
│   ├── 📄 Dashboard.jsx                # Main dashboard router
│   ├── 📄 Unauthorized.jsx             # 403 error page
│   │
│   ├── 📂 user/                        # User Portal Pages
│   │   ├── UserDashboard.jsx           # User home dashboard
│   │   ├── CreateRequest.jsx           # Create new request form
│   │   ├── MyRequests.jsx              # User's request list
│   │   ├── RequestDetails.jsx          # Request detail view
│   │   └── Profile.jsx                 # User profile page
│   │
│   ├── 📂 admin/                       # Admin Dashboard Pages
│   │   ├── AdminDashboard.jsx          # Admin home with stats
│   │   ├── RequestManagement.jsx       # Manage all requests
│   │   ├── UserManagement.jsx          # User CRUD operations
│   │   ├── CategoryManagement.jsx      # Category management
│   │   ├── ServiceCatalog.jsx          # Service catalog config
│   │   └── Reports.jsx                 # Reports & analytics
│   │
│   └── 📂 department/                  # Department Dashboard Pages
│       ├── DepartmentDashboard.jsx     # Department home
│       ├── AssignedRequests.jsx        # Dept assigned requests
│       └── RequestWorkflow.jsx         # Request processing
│
├── 📂 components/                      # Reusable Components
│   │
│   ├── 📂 common/                      # Common UI Components
│   │   ├── Navbar.jsx                  # Navigation bar
│   │   ├── Sidebar.jsx                 # Side navigation
│   │   ├── Footer.jsx                  # Footer component
│   │   ├── LoadingSpinner.jsx          # Loading indicator
│   │   ├── ErrorBoundary.jsx           # Error boundary
│   │   └── ProtectedRoute.jsx          # Route guard
│   │
│   ├── 📂 request/                     # Request-related Components
│   │   ├── RequestCard.jsx             # Request card display
│   │   ├── RequestList.jsx             # Request list view
│   │   ├── RequestForm.jsx             # Request form
│   │   ├── RequestFilters.jsx          # Filter controls
│   │   ├── StatusBadge.jsx             # Status indicator
│   │   ├── PriorityBadge.jsx           # Priority indicator
│   │   ├── ViewDetailsModal.jsx        # Request details modal
│   │   ├── CommentSection.jsx          # Comments display
│   │   └── Timeline.jsx                # Request timeline
│   │
│   └── 📂 admin/                       # Admin-specific Components
│       ├── UserTable.jsx               # User data table
│       ├── UserFormModal.jsx           # User create/edit form
│       ├── DeleteConfirmDialog.jsx     # Delete confirmation
│       ├── AssignDepartmentModal.jsx   # Dept assignment modal
│       ├── StatsCard.jsx               # Statistics card
│       └── CategoryForm.jsx            # Category form
│
├── 📂 services/                        # API Service Layer
│   ├── authService.js                  # Authentication API calls
│   ├── requestService.js               # Request API calls
│   ├── adminService.js                 # Admin API calls
│   ├── departmentService.js            # Department API calls
│   ├── userService.js                  # User API calls
│   └── categoryService.js              # Category API calls
│
├── 📂 context/                         # React Context Providers
│   ├── AuthContext.jsx                 # Authentication state
│   └── RequestContext.jsx              # Request data state
│
├── 📂 api/                             # API Configuration
│   └── axios.js                        # Axios instance config
│
├── 📂 utils/                           # Utility Functions
│   ├── dateFormatter.js                # Date formatting
│   ├── validators.js                   # Form validation
│   └── constants.js                    # App constants
│
└── 📂 styles/                          # Global Styles
    └── theme.js                        # MUI theme config
```

---

## 🎨 Component Hierarchy

### Main Application Flow

```
App.jsx
├── AuthProvider (Context)
│   └── Router
│       ├── Public Routes
│       │   ├── /login → Login.jsx
│       │   └── /register → Register.jsx
│       │
│       └── Protected Routes
│           ├── /dashboard → Dashboard.jsx
│           │   ├── ROLE_USER → UserDashboard.jsx
│           │   ├── ROLE_ADMIN → AdminDashboard.jsx
│           │   └── ROLE_DEPARTMENT → DepartmentDashboard.jsx
│           │
│           ├── /requests/create → CreateRequest.jsx
│           ├── /requests/my-requests → MyRequests.jsx
│           ├── /requests/:id → RequestDetails.jsx
│           │
│           ├── /admin/requests → RequestManagement.jsx
│           ├── /admin/users → UserManagement.jsx
│           ├── /admin/categories → CategoryManagement.jsx
│           │
│           └── /department/requests → AssignedRequests.jsx
```

---

## 🔐 Authentication Flow

### AuthContext Implementation

```javascript
// context/AuthContext.jsx
export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    // Check for stored token on mount
    const token = localStorage.getItem('token');
    if (token) {
      loadUser(token);
    } else {
      setLoading(false);
    }
  }, []);
  
  const login = async (credentials) => {
    const response = await authService.login(credentials);
    localStorage.setItem('token', response.token);
    setUser(response.user);
  };
  
  const logout = () => {
    localStorage.removeItem('token');
    setUser(null);
  };
  
  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};
```

### Protected Route Component

```javascript
// components/common/ProtectedRoute.jsx
const ProtectedRoute = ({ children, allowedRoles }) => {
  const { user, loading } = useContext(AuthContext);
  
  if (loading) return <LoadingSpinner />;
  
  if (!user) return <Navigate to="/login" />;
  
  if (allowedRoles && !user.roles.some(r => allowedRoles.includes(r))) {
    return <Navigate to="/unauthorized" />;
  }
  
  return children;
};
```

---

## 📡 API Service Layer

### Axios Configuration

```javascript
// api/axios.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Request interceptor - Add JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - Handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### Service Pattern Example

```javascript
// services/requestService.js
import api from '../api/axios';

export const requestService = {
  // Get user's requests
  getMyRequests: async () => {
    const response = await api.get('/requests/my-requests');
    return response.data;
  },
  
  // Create new request
  createRequest: async (requestData) => {
    const response = await api.post('/requests', requestData);
    return response.data;
  },
  
  // Get request by ID
  getRequestById: async (id) => {
    const response = await api.get(`/requests/${id}`);
    return response.data;
  },
  
  // Update request
  updateRequest: async (id, data) => {
    const response = await api.put(`/requests/${id}`, data);
    return response.data;
  },
  
  // Delete request
  deleteRequest: async (id) => {
    await api.delete(`/requests/${id}`);
  },
  
  // Add comment
  addComment: async (id, comment) => {
    const response = await api.post(`/requests/${id}/comments`, comment);
    return response.data;
  }
};
```

---

## 🎯 Key Pages Explained

### 1. Login Page (`Login.jsx`)

**Purpose:** User authentication  
**Features:**
- Email/username and password input
- Form validation
- JWT token storage
- Redirect to dashboard on success
- Error handling

**Key Code:**
```javascript
const handleLogin = async (e) => {
  e.preventDefault();
  try {
    await login({ username, password });
    navigate('/dashboard');
  } catch (error) {
    setError('Invalid credentials');
  }
};
```

### 2. User Dashboard (`UserDashboard.jsx`)

**Purpose:** User home page  
**Features:**
- Quick stats (total requests, pending, resolved)
- Recent requests list
- Quick action buttons
- Navigation to create request

### 3. Create Request (`CreateRequest.jsx`)

**Purpose:** Submit new service request  
**Features:**
- Category selection
- Request type dropdown (filtered by category)
- Service selection (optional)
- Title and description input
- Priority selection
- Form validation

**Flow:**
```
1. Select Category → 2. Select Request Type → 3. Fill Details → 4. Submit
```

### 4. Admin Dashboard (`AdminDashboard.jsx`)

**Purpose:** Admin control center  
**Features:**
- System statistics (total users, requests, departments)
- Request status breakdown chart
- Recent activity feed
- Quick actions (create user, manage categories)
- Navigation to management pages

### 5. Request Management (`RequestManagement.jsx`)

**Purpose:** Admin request oversight  
**Features:**
- All requests table with filters
- Status and priority badges
- Assign to department
- Update status/priority
- Delete requests
- View details modal

### 6. Department Dashboard (`DepartmentDashboard.jsx`)

**Purpose:** Department request handling  
**Features:**
- Assigned requests list
- Status update controls
- Work notes section
- Request user clarification
- Resolve with summary
- Department metrics

---

## 🧩 Reusable Components

### RequestCard Component

```javascript
// components/request/RequestCard.jsx
const RequestCard = ({ request, onViewDetails, onUpdate }) => {
  return (
    <Card>
      <CardHeader
        title={request.title}
        subheader={`Created: ${formatDate(request.createdAt)}`}
        action={
          <>
            <StatusBadge status={request.status} />
            <PriorityBadge priority={request.priority} />
          </>
        }
      />
      <CardContent>
        <Typography variant="body2">
          {request.description}
        </Typography>
        <Chip label={request.category} />
        <Chip label={request.requestType} />
      </CardContent>
      <CardActions>
        <Button onClick={() => onViewDetails(request.id)}>
          View Details
        </Button>
        {onUpdate && (
          <Button onClick={() => onUpdate(request)}>
            Update
          </Button>
        )}
      </CardActions>
    </Card>
  );
};
```

### StatusBadge Component

```javascript
// components/request/StatusBadge.jsx
const StatusBadge = ({ status }) => {
  const getColor = (status) => {
    switch (status) {
      case 'NEW': return 'info';
      case 'IN_PROGRESS': return 'warning';
      case 'RESOLVED': return 'success';
      case 'CLOSED': return 'default';
      case 'REJECTED': return 'error';
      default: return 'default';
    }
  };
  
  return (
    <Chip 
      label={status} 
      color={getColor(status)} 
      size="small" 
    />
  );
};
```

---

## 🎨 Material-UI Theme Configuration

```javascript
// styles/theme.js
import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
      light: '#42a5f5',
      dark: '#1565c0',
    },
    secondary: {
      main: '#dc004e',
    },
    success: {
      main: '#4caf50',
    },
    warning: {
      main: '#ff9800',
    },
    error: {
      main: '#f44336',
    },
  },
  typography: {
    fontFamily: 'Roboto, Arial, sans-serif',
    h4: {
      fontWeight: 600,
    },
    h6: {
      fontWeight: 500,
    },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          borderRadius: 8,
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        },
      },
    },
  },
});
```

---

## 🔄 State Management Patterns

### Using Context for Global State

```javascript
// Example: Using AuthContext in a component
const MyComponent = () => {
  const { user, logout } = useContext(AuthContext);
  
  return (
    <div>
      <p>Welcome, {user.firstName}!</p>
      <Button onClick={logout}>Logout</Button>
    </div>
  );
};
```

### Local State with useState

```javascript
// Example: Form state management
const CreateRequestForm = () => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    categoryId: '',
    requestTypeId: '',
    priority: 'MEDIUM'
  });
  
  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    await requestService.createRequest(formData);
  };
  
  return (
    <form onSubmit={handleSubmit}>
      {/* Form fields */}
    </form>
  );
};
```

---

## 🛣️ Routing Configuration

```javascript
// App.jsx
import { BrowserRouter, Routes, Route } from 'react-router-dom';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          
          {/* Protected Routes */}
          <Route path="/dashboard" element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } />
          
          {/* Admin Routes */}
          <Route path="/admin/*" element={
            <ProtectedRoute allowedRoles={['ROLE_ADMIN']}>
              <AdminRoutes />
            </ProtectedRoute>
          } />
          
          {/* Department Routes */}
          <Route path="/department/*" element={
            <ProtectedRoute allowedRoles={['ROLE_DEPARTMENT']}>
              <DepartmentRoutes />
            </ProtectedRoute>
          } />
          
          {/* Fallback */}
          <Route path="*" element={<Navigate to="/dashboard" />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
```

---

## 📱 Responsive Design

### Mobile-First Approach

```javascript
import { useMediaQuery, useTheme } from '@mui/material';

const MyComponent = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const isTablet = useMediaQuery(theme.breakpoints.down('md'));
  
  return (
    <Grid container spacing={isMobile ? 1 : 3}>
      <Grid item xs={12} md={6} lg={4}>
        {/* Content */}
      </Grid>
    </Grid>
  );
};
```

---

## ⚡ Performance Optimization

### Code Splitting with Lazy Loading

```javascript
import { lazy, Suspense } from 'react';

const AdminDashboard = lazy(() => import('./pages/admin/AdminDashboard'));

function App() {
  return (
    <Suspense fallback={<LoadingSpinner />}>
      <AdminDashboard />
    </Suspense>
  );
}
```

### Memoization

```javascript
import { useMemo, useCallback } from 'react';

const RequestList = ({ requests }) => {
  // Memoize expensive calculations
  const filteredRequests = useMemo(() => {
    return requests.filter(r => r.status !== 'CLOSED');
  }, [requests]);
  
  // Memoize callbacks
  const handleDelete = useCallback((id) => {
    // Delete logic
  }, []);
  
  return (
    <div>
      {filteredRequests.map(request => (
        <RequestCard 
          key={request.id} 
          request={request}
          onDelete={handleDelete}
        />
      ))}
    </div>
  );
};
```

---

## 🧪 Testing Approach

### Component Testing with React Testing Library

```javascript
import { render, screen, fireEvent } from '@testing-library/react';
import Login from './Login';

test('renders login form', () => {
  render(<Login />);
  expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
  expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
});

test('handles form submission', async () => {
  render(<Login />);
  
  fireEvent.change(screen.getByLabelText(/username/i), {
    target: { value: 'testuser' }
  });
  fireEvent.change(screen.getByLabelText(/password/i), {
    target: { value: 'password123' }
  });
  
  fireEvent.click(screen.getByRole('button', { name: /login/i }));
  
  // Assert expected behavior
});
```

---

## 📦 Build Configuration

### Vite Configuration

```javascript
// vite.config.js
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  },
  build: {
    outDir: 'dist',
    sourcemap: true,
  }
});
```

---

## 🎯 Best Practices Implemented

1. **Component Composition** - Small, reusable components
2. **Props Validation** - PropTypes for type checking
3. **Error Boundaries** - Graceful error handling
4. **Accessibility** - ARIA labels and semantic HTML
5. **Code Splitting** - Lazy loading for better performance
6. **Consistent Naming** - Clear, descriptive names
7. **Separation of Concerns** - Services, components, pages
8. **Context for Global State** - Avoid prop drilling
9. **Custom Hooks** - Reusable logic extraction
10. **Environment Variables** - Configuration management

---

## 🚀 Development Workflow

1. **Start Development Server**
   ```bash
   npm run dev
   ```

2. **Build for Production**
   ```bash
   npm run build
   ```

3. **Preview Production Build**
   ```bash
   npm run preview
   ```

---

**Last Updated:** December 2025
