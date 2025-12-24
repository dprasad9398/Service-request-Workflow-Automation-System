import api from '../api/axios';

/**
 * Department Service
 * Handles API calls for department and agent management
 */
const departmentService = {
    /**
     * Get all active departments
     */
    getAllDepartments: async () => {
        const response = await api.get('/admin/departments');
        return response.data;
    },

    /**
     * Get agents by department
     */
    getAgentsByDepartment: async (departmentId) => {
        const response = await api.get(`/admin/departments/${departmentId}/agents`);
        return response.data;
    },

    /**
     * Get all agents
     */
    getAllAgents: async () => {
        const response = await api.get('/admin/departments/agents');
        return response.data;
    }
};

export default departmentService;
