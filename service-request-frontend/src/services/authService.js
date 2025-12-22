import api from '../api/axios';

/**
 * Authentication Service
 * Handles login, register, and user authentication
 */
const authService = {
    /**
     * Login user
     */
    login: async (username, password) => {
        const response = await api.post('/auth/login', { username, password });
        if (response.data.accessToken) {
            localStorage.setItem('token', response.data.accessToken);
            localStorage.setItem('user', JSON.stringify(response.data));
        }
        return response.data;
    },

    /**
     * Register new user
     */
    register: async (userData) => {
        const response = await api.post('/auth/register', userData);
        return response.data;
    },

    /**
     * Logout user
     */
    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
    },

    /**
     * Get current user from localStorage
     */
    getCurrentUser: () => {
        const userStr = localStorage.getItem('user');
        return userStr ? JSON.parse(userStr) : null;
    },

    /**
     * Check if user is authenticated
     */
    isAuthenticated: () => {
        return !!localStorage.getItem('token');
    },

    /**
     * Get user roles
     */
    getUserRoles: () => {
        const user = authService.getCurrentUser();
        return user?.roles || [];
    },

    /**
     * Check if user has specific role
     */
    hasRole: (role) => {
        const roles = authService.getUserRoles();
        return roles.includes(role);
    }
};

export default authService;
