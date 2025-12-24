import React from 'react';
import { Card, CardContent, Typography, Box } from '@mui/material';
import {
    Computer,
    People,
    Business,
    HelpOutline
} from '@mui/icons-material';

/**
 * Category Selection Card Component
 * Displays a clickable card for each request category
 */
const CategoryCard = ({ category, onClick, selected }) => {
    // Icon mapping
    const getIcon = (iconName) => {
        const icons = {
            'computer': <Computer sx={{ fontSize: 48 }} />,
            'people': <People sx={{ fontSize: 48 }} />,
            'building': <Business sx={{ fontSize: 48 }} />,
            'help-circle': <HelpOutline sx={{ fontSize: 48 }} />
        };
        return icons[iconName] || <HelpOutline sx={{ fontSize: 48 }} />;
    };

    return (
        <Card
            onClick={() => onClick(category)}
            sx={{
                cursor: 'pointer',
                transition: 'all 0.3s',
                border: selected ? '2px solid #1976d2' : '2px solid transparent',
                backgroundColor: selected ? '#e3f2fd' : 'white',
                '&:hover': {
                    transform: 'translateY(-4px)',
                    boxShadow: 4
                }
            }}
        >
            <CardContent>
                <Box sx={{ textAlign: 'center' }}>
                    <Box sx={{ color: selected ? '#1976d2' : '#666', mb: 2 }}>
                        {getIcon(category.icon)}
                    </Box>
                    <Typography variant="h6" gutterBottom>
                        {category.name}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                        {category.description}
                    </Typography>
                </Box>
            </CardContent>
        </Card>
    );
};

export default CategoryCard;
