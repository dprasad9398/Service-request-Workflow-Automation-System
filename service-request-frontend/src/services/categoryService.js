import api from '../api/axios';

/**
 * Category Service
 * Handles API calls for service categories and request types
 * Used by Create Request flow
 */
const categoryService = {
    /**
     * Get all active service categories
     * GET /api/service-categories
     */
    getCategories: async () => {
        console.log('📡 API Call: GET /api/service-categories');
        try {
            const response = await api.get('/service-categories');
            console.log('✅ Categories loaded:', response.data.length, 'categories');
            console.log('Categories:', response.data);
            return response.data;
        } catch (error) {
            console.error('❌ Error loading categories:', error);
            console.error('Error details:', error.response?.data);
            throw error;
        }
    },

    /**
     * Get category by ID
     * GET /api/service-categories/{id}
     */
    getCategoryById: async (categoryId) => {
        console.log(`📡 API Call: GET /api/service-categories/${categoryId}`);
        try {
            const response = await api.get(`/service-categories/${categoryId}`);
            console.log('✅ Category loaded:', response.data);
            return response.data;
        } catch (error) {
            console.error(`❌ Error loading category ${categoryId}:`, error);
            throw error;
        }
    },

    /**
     * Get request types for a category
     * GET /api/service-categories/{categoryId}/types
     */
    getCategoryTypes: async (categoryId) => {
        console.log(`📡 API Call: GET /api/service-categories/${categoryId}/types`);
        try {
            const response = await api.get(`/service-categories/${categoryId}/types`);
            console.log('✅ Request types loaded:', response.data.length, 'types');
            console.log('Request types:', response.data);
            return response.data;
        } catch (error) {
            console.error(`❌ Error loading request types for category ${categoryId}:`, error);
            console.error('Error details:', error.response?.data);
            throw error;
        }
    },

    /**
     * Get all active request types
     * GET /api/service-categories/types/all
     */
    getAllTypes: async () => {
        console.log('📡 API Call: GET /api/service-categories/types/all');
        try {
            const response = await api.get('/service-categories/types/all');
            console.log('✅ All request types loaded:', response.data.length, 'types');
            return response.data;
        } catch (error) {
            console.error('❌ Error loading all request types:', error);
            throw error;
        }
    }
};

export default categoryService;
