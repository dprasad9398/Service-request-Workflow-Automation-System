import api from '../api/axios';

/**
 * System Settings Service
 * Handles API calls for system settings management
 */
const settingsService = {
    /**
     * Get all settings
     */
    getAllSettings: async () => {
        const response = await api.get('/admin/settings');
        return response.data;
    },

    /**
     * Get settings by category
     */
    getSettingsByCategory: async (category) => {
        const response = await api.get(`/admin/settings/category/${category}`);
        return response.data;
    },

    /**
     * Update setting
     */
    updateSetting: async (key, value, category, description) => {
        const response = await api.put(`/admin/settings/${key}`, {
            value,
            category,
            description
        });
        return response.data;
    },

    /**
     * Bulk update settings
     */
    bulkUpdateSettings: async (category, settings) => {
        const response = await api.post('/admin/settings/bulk', {
            category,
            settings
        });
        return response.data;
    }
};

export default settingsService;
