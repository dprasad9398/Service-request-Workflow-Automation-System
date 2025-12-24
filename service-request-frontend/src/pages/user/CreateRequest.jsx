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
    Stepper,
    Step,
    StepLabel
} from '@mui/material';
import CategoryCard from '../../components/request/CategoryCard';
import categoryService from '../../services/categoryService';

/**
 * Create Request Page
 * Allows users to create categorized service requests
 */
const CreateRequest = () => {
    const navigate = useNavigate();
    const [activeStep, setActiveStep] = useState(0);
    const [categories, setCategories] = useState([]);
    const [types, setTypes] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const [formData, setFormData] = useState({
        categoryId: null,
        typeId: null,
        title: '',
        description: '',
        priority: 'MEDIUM',
        customFields: {}
    });

    const steps = ['Select Category', 'Select Type', 'Request Details'];

    useEffect(() => {
        loadCategories();
    }, []);

    const loadCategories = async () => {
        try {
            const data = await categoryService.getCategories();
            setCategories(data);
        } catch (err) {
            setError('Failed to load categories');
        }
    };

    const handleCategorySelect = async (category) => {
        setError(''); // Clear any previous errors
        setFormData({ ...formData, categoryId: category.id, typeId: null });
        try {
            console.log('Loading types for category:', category.id);
            const typesData = await categoryService.getCategoryTypes(category.id);
            console.log('Types loaded:', typesData);
            setTypes(typesData);
            setActiveStep(1);
        } catch (err) {
            console.error('Error loading types:', err);
            setError('Failed to load request types');
        }
    };

    const handleTypeSelect = (typeId) => {
        setFormData({ ...formData, typeId });
        setActiveStep(2);
    };

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            // TODO: Call API to create request
            console.log('Creating request:', formData);
            setSuccess('Request created successfully!');
            setTimeout(() => navigate('/user/requests'), 2000);
        } catch (err) {
            setError('Failed to create request');
            setLoading(false);
        }
    };

    const handleBack = () => {
        setActiveStep((prev) => prev - 1);
    };

    return (
        <Container maxWidth="lg">
            <Box sx={{ mt: 4, mb: 4 }}>
                <Typography variant="h4" gutterBottom>
                    Create New Request
                </Typography>

                <Stepper activeStep={activeStep} sx={{ mt: 3, mb: 4 }}>
                    {steps.map((label) => (
                        <Step key={label}>
                            <StepLabel>{label}</StepLabel>
                        </Step>
                    ))}
                </Stepper>

                {error && (
                    <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>
                        {error}
                    </Alert>
                )}

                {success && (
                    <Alert severity="success" sx={{ mb: 2 }}>
                        {success}
                    </Alert>
                )}

                {/* Step 1: Select Category */}
                {activeStep === 0 && (
                    <Box>
                        <Typography variant="h6" gutterBottom>
                            Select Request Category
                        </Typography>
                        <Grid container spacing={3} sx={{ mt: 2 }}>
                            {categories.map((category) => (
                                <Grid item xs={12} sm={6} md={3} key={category.id}>
                                    <CategoryCard
                                        category={category}
                                        onClick={handleCategorySelect}
                                        selected={formData.categoryId === category.id}
                                    />
                                </Grid>
                            ))}
                        </Grid>
                    </Box>
                )}

                {/* Step 2: Select Type */}
                {activeStep === 1 && (
                    <Box>
                        <Typography variant="h6" gutterBottom>
                            Select Request Type
                        </Typography>
                        <Grid container spacing={2} sx={{ mt: 2 }}>
                            {types.map((type) => (
                                <Grid item xs={12} sm={6} key={type.id}>
                                    <Paper
                                        onClick={() => handleTypeSelect(type.id)}
                                        sx={{
                                            p: 2,
                                            cursor: 'pointer',
                                            border: formData.typeId === type.id ? '2px solid #1976d2' : '1px solid #ddd',
                                            '&:hover': { boxShadow: 2 }
                                        }}
                                    >
                                        <Typography variant="h6">{type.name}</Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            {type.description}
                                        </Typography>
                                    </Paper>
                                </Grid>
                            ))}
                        </Grid>
                        <Button onClick={handleBack} sx={{ mt: 2 }}>
                            Back
                        </Button>
                    </Box>
                )}

                {/* Step 3: Request Details */}
                {activeStep === 2 && (
                    <Box component="form" onSubmit={handleSubmit}>
                        <Typography variant="h6" gutterBottom>
                            Request Details
                        </Typography>
                        <Grid container spacing={2} sx={{ mt: 2 }}>
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
                                {loading ? 'Creating...' : 'Create Request'}
                            </Button>
                        </Box>
                    </Box>
                )}
            </Box>
        </Container>
    );
};

export default CreateRequest;
