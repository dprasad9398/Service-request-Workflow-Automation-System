import api from '../api/axios';

/**
 * Category Service
 * Handles API calls for request categories and types
 */
const categoryService = {
    /**
     * Get all active categories
     */
    getCategories: async () => {
        const response = await api.get('/user/categories');
        return response.data;
    },

    /**
     * Get category by ID
     */
    getCategoryById: async (categoryId) => {
        const response = await api.get(`/user/categories/${categoryId}`);
        return response.data;
    },

    /**
     * Get request types for a category
     */
    getCategoryTypes: async (categoryId) => {
        const response = await api.get(`/user/categories/${categoryId}/types`);
        return response.data;
    },

    /**
     * Get all active request types
     */
    getAllTypes: async () => {
        const response = await api.get('/user/categories/types/all');
        return response.data;
    }
};

export default categoryService;
