import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Container,
    Box,
    Typography,
    Grid,
    TextField,
    Button,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Alert,
    Paper,
    Card,
    CardContent,
    CardActionArea,
    Chip,
    CircularProgress
} from '@mui/material';
import serviceCatalogService from '../../services/serviceCatalogService';
import requestService from '../../services/requestService';

/**
 * Create Request Page
 * Allows users to create service requests by selecting from catalog
 */
const CreateRequest = () => {
    const navigate = useNavigate();
    const [categories, setCategories] = useState([]);
    const [services, setServices] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState(null);
    const [selectedService, setSelectedService] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const [formData, setFormData] = useState({
        title: '',
        description: '',
        priority: 'MEDIUM'
    });

    useEffect(() => {
        loadCategories();
    }, []);

    const loadCategories = async () => {
        try {
            const data = await serviceCatalogService.getActiveCategories();
            setCategories(data);
        } catch (err) {
            setError('Failed to load categories');
            console.error(err);
        }
    };

    const handleCategorySelect = async (category) => {
        setSelectedCategory(category);
        setSelectedService(null);
        setError('');

        try {
            const servicesData = await serviceCatalogService.getServicesByCategory(category.id);
            setServices(servicesData);
        } catch (err) {
            setError('Failed to load services');
            console.error(err);
        }
    };

    const handleServiceSelect = (service) => {
        setSelectedService(service);
        // Auto-fill priority and department from service
        setFormData({
            ...formData,
            priority: service.defaultPriority || 'MEDIUM'
        });
    };

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!selectedService) {
            setError('Please select a service');
            return;
        }

        setLoading(true);
        setError('');

        try {
            const requestData = {
                serviceId: selectedService.id,
                categoryId: selectedCategory.id,
                title: formData.title,
                description: formData.description,
                priority: formData.priority
            };

            await requestService.createRequest(requestData);
            navigate('/my-requests', { replace: true, state: { reload: true } });
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to create request');
            setLoading(false);
        }
    };

    const handleBack = () => {
        if (selectedService) {
            setSelectedService(null);
        } else if (selectedCategory) {
            setSelectedCategory(null);
            setServices([]);
        }
    };

    return (
        <Container maxWidth="md">
            <Box sx={{ mt: 4, mb: 4 }}>
                <Typography variant="h4" gutterBottom>
                    Create New Request
                </Typography>

                {error && (
                    <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>
                        {error}
                    </Alert>
                )}

                {/* Step 1: Select Category */}
                {!selectedCategory && (
                    <Box>
                        <Typography variant="h6" gutterBottom sx={{ mt: 3 }}>
                            Step 1: Select Category
                        </Typography>
                        <Grid container spacing={2}>
                            {categories.map((category) => (
                                <Grid item xs={12} sm={6} md={4} key={category.id}>
                                    <Card>
                                        <CardActionArea onClick={() => handleCategorySelect(category)}>
                                            <CardContent>
                                                <Typography variant="h6" gutterBottom>
                                                    {category.name}
                                                </Typography>
                                                <Typography variant="body2" color="text.secondary">
                                                    {category.description}
                                                </Typography>
                                                <Chip
                                                    label={`${category.serviceCount || 0} services`}
                                                    size="small"
                                                    sx={{ mt: 1 }}
                                                />
                                            </CardContent>
                                        </CardActionArea>
                                    </Card>
                                </Grid>
                            ))}
                        </Grid>
                    </Box>
                )}

                {/* Step 2: Select Service */}
                {selectedCategory && !selectedService && (
                    <Box>
                        <Typography variant="h6" gutterBottom sx={{ mt: 3 }}>
                            Step 2: Select Service
                        </Typography>
                        <Typography variant="body2" color="text.secondary" gutterBottom>
                            Category: {selectedCategory.name}
                        </Typography>
                        <Grid container spacing={2} sx={{ mt: 1 }}>
                            {services.map((service) => (
                                <Grid item xs={12} key={service.id}>
                                    <Card>
                                        <CardActionArea onClick={() => handleServiceSelect(service)}>
                                            <CardContent>
                                                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                                                    <Box>
                                                        <Typography variant="h6">
                                                            {service.name}
                                                        </Typography>
                                                        <Typography variant="body2" color="text.secondary">
                                                            {service.description}
                                                        </Typography>
                                                    </Box>
                                                    <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                                                        {service.defaultPriority && (
                                                            <Chip
                                                                label={service.defaultPriority}
                                                                size="small"
                                                                color={
                                                                    service.defaultPriority === 'HIGH' ? 'error' :
                                                                        service.defaultPriority === 'MEDIUM' ? 'warning' : 'default'
                                                                }
                                                            />
                                                        )}
                                                        {service.department && (
                                                            <Chip
                                                                label={service.department}
                                                                size="small"
                                                                variant="outlined"
                                                            />
                                                        )}
                                                        {service.slaHours && (
                                                            <Chip
                                                                label={`SLA: ${service.slaHours}h`}
                                                                size="small"
                                                                variant="outlined"
                                                            />
                                                        )}
                                                    </Box>
                                                </Box>
                                            </CardContent>
                                        </CardActionArea>
                                    </Card>
                                </Grid>
                            ))}
                        </Grid>
                        <Button onClick={handleBack} sx={{ mt: 2 }}>
                            Back to Categories
                        </Button>
                    </Box>
                )}

                {/* Step 3: Request Details */}
                {selectedService && (
                    <Paper elevation={3} sx={{ p: 3, mt: 3 }}>
                        <Typography variant="h6" gutterBottom>
                            Step 3: Request Details
                        </Typography>
                        <Typography variant="body2" color="text.secondary" gutterBottom>
                            Service: {selectedService.name}
                        </Typography>

                        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3 }}>
                            <Grid container spacing={3}>
                                <Grid item xs={12}>
                                    <TextField
                                        fullWidth
                                        label="Title"
                                        name="title"
                                        value={formData.title}
                                        onChange={handleChange}
                                        required
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <TextField
                                        fullWidth
                                        label="Description"
                                        name="description"
                                        value={formData.description}
                                        onChange={handleChange}
                                        multiline
                                        rows={4}
                                        required
                                    />
                                </Grid>
                                <Grid item xs={12} sm={6}>
                                    <FormControl fullWidth>
                                        <InputLabel>Priority</InputLabel>
                                        <Select
                                            name="priority"
                                            value={formData.priority}
                                            onChange={handleChange}
                                            label="Priority"
                                        >
                                            <MenuItem value="LOW">Low</MenuItem>
                                            <MenuItem value="MEDIUM">Medium</MenuItem>
                                            <MenuItem value="HIGH">High</MenuItem>
                                            <MenuItem value="CRITICAL">Critical</MenuItem>
                                        </Select>
                                    </FormControl>
                                </Grid>
                                {selectedService.department && (
                                    <Grid item xs={12} sm={6}>
                                        <TextField
                                            fullWidth
                                            label="Department"
                                            value={selectedService.department}
                                            disabled
                                        />
                                    </Grid>
                                )}
                            </Grid>

                            <Box sx={{ mt: 3, display: 'flex', gap: 2 }}>
                                <Button onClick={handleBack}>
                                    Back
                                </Button>
                                <Button
                                    type="submit"
                                    variant="contained"
                                    disabled={loading}
                                >
                                    {loading ? <CircularProgress size={24} /> : 'Submit Request'}
                                </Button>
                            </Box>
                        </Box>
                    </Paper>
                )}
            </Box>
        </Container>
    );
};

export default CreateRequest;
