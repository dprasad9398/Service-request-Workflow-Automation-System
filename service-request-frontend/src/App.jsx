import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/Layout';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import CreateRequest from './pages/CreateRequest';
import MyRequests from './pages/MyRequests';
import RequestDetails from './pages/RequestDetails';
import ApproverDashboard from './pages/ApproverDashboard';
import AgentDashboard from './pages/AgentDashboard';

const theme = createTheme({
    palette: {
        primary: {
            main: '#1976d2',
        },
        secondary: {
            main: '#dc004e',
        },
    },
});

// Wrapper component for pages with layout
const PageWithLayout = ({ children }) => {
    return <Layout>{children}</Layout>;
};

function App() {
    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <AuthProvider>
                <Router>
                    <Routes>
                        {/* Public routes without layout */}
                        <Route path="/login" element={<Login />} />
                        <Route path="/register" element={<Register />} />

                        {/* Protected routes with layout */}
                        <Route
                            path="/dashboard"
                            element={
                                <ProtectedRoute>
                                    <PageWithLayout>
                                        <Dashboard />
                                    </PageWithLayout>
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="/create-request"
                            element={
                                <ProtectedRoute>
                                    <PageWithLayout>
                                        <CreateRequest />
                                    </PageWithLayout>
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="/my-requests"
                            element={
                                <ProtectedRoute>
                                    <PageWithLayout>
                                        <MyRequests />
                                    </PageWithLayout>
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="/request/:id"
                            element={
                                <ProtectedRoute>
                                    <PageWithLayout>
                                        <RequestDetails />
                                    </PageWithLayout>
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="/approvals"
                            element={
                                <ProtectedRoute requiredRole="ROLE_APPROVER">
                                    <PageWithLayout>
                                        <ApproverDashboard />
                                    </PageWithLayout>
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="/tasks"
                            element={
                                <ProtectedRoute requiredRole="ROLE_AGENT">
                                    <PageWithLayout>
                                        <AgentDashboard />
                                    </PageWithLayout>
                                </ProtectedRoute>
                            }
                        />
                        <Route path="/" element={<Navigate to="/dashboard" replace />} />
                    </Routes>
                </Router>
            </AuthProvider>
        </ThemeProvider>
    );
}

export default App;
