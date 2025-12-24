import api from '../api/axios';

/**
 * Service Catalog Service
 * Handles service catalog API calls
 */
const serviceCatalogService = {
    // Admin APIs
    /**
     * Get all services (admin)
     */
    getAllServices: async () => {
        const response = await api.get('/admin/service-catalog');
        return response.data;
    },

    /**
     * Get service by ID
     */
    getServiceById: async (id) => {
        const response = await api.get(`/admin/service-catalog/${id}`);
        return response.data;
    },

    /**
     * Create new service
     */
    createService: async (serviceData) => {
        const response = await api.post('/admin/service-catalog', serviceData);
        return response.data;
    },

    /**
     * Update service
     */
    updateService: async (id, serviceData) => {
        const response = await api.put(`/admin/service-catalog/${id}`, serviceData);
        return response.data;
    },

    /**
     * Delete service (soft delete)
     */
    deleteService: async (id) => {
        const response = await api.delete(`/admin/service-catalog/${id}`);
        return response.data;
    },

    /**
     * Get all categories (admin)
     */
    getAllCategories: async () => {
        const response = await api.get('/admin/service-catalog/categories');
        return response.data;
    },

    /**
     * Create category
     */
    createCategory: async (categoryData) => {
        const response = await api.post('/admin/service-catalog/categories', categoryData);
        return response.data;
    },

    /**
     * Update category
     */
    updateCategory: async (id, categoryData) => {
        const response = await api.put(`/admin/service-catalog/categories/${id}`, categoryData);
        return response.data;
    },

    // User APIs
    /**
     * Get active categories (user)
     */
    getActiveCategories: async () => {
        const response = await api.get('/user/service-catalog/categories');
        return response.data;
    },

    /**
     * Get services by category (user)
     */
    getServicesByCategory: async (categoryId) => {
        const response = await api.get(`/user/service-catalog/categories/${categoryId}/services`);
        return response.data;
    },

    /**
     * Get all active services (user)
     */
    getActiveServices: async () => {
        const response = await api.get('/user/service-catalog/services');
        return response.data;
    }
};

export default serviceCatalogService;
