import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
    AppBar,
    Toolbar,
    Typography,
    Button,
    Box,
    IconButton,
    Menu,
    MenuItem,
    Badge
} from '@mui/material';
import {
    AccountCircle,
    Notifications,
    Dashboard as DashboardIcon,
    Assignment,
    Approval,
    Work
} from '@mui/icons-material';

const Layout = ({ children }) => {
    const navigate = useNavigate();
    const location = useLocation();
    const { user, logout, hasRole } = useAuth();
    const [anchorEl, setAnchorEl] = React.useState(null);

    const handleMenu = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleLogout = () => {
        handleClose();
        logout();
    };

    const isActive = (path) => location.pathname === path;

    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <AppBar position="static">
                <Toolbar>
                    <Typography variant="h6" component="div" sx={{ flexGrow: 0, mr: 4 }}>
                        Service Desk
                    </Typography>

                    <Box sx={{ flexGrow: 1, display: 'flex', gap: 2 }}>
                        <Button
                            color="inherit"
                            startIcon={<DashboardIcon />}
                            onClick={() => navigate('/dashboard')}
                            sx={{
                                backgroundColor: isActive('/dashboard') ? 'rgba(255,255,255,0.1)' : 'transparent'
                            }}
                        >
                            Dashboard
                        </Button>

                        {hasRole('ROLE_END_USER') && (
                            <>
                                <Button
                                    color="inherit"
                                    startIcon={<Assignment />}
                                    onClick={() => navigate('/my-requests')}
                                    sx={{
                                        backgroundColor: isActive('/my-requests') ? 'rgba(255,255,255,0.1)' : 'transparent'
                                    }}
                                >
                                    My Requests
                                </Button>
                                <Button
                                    color="inherit"
                                    onClick={() => navigate('/create-request')}
                                    variant={isActive('/create-request') ? 'outlined' : 'text'}
                                >
                                    New Request
                                </Button>
                            </>
                        )}

                        {hasRole('ROLE_APPROVER') && (
                            <Button
                                color="inherit"
                                startIcon={<Approval />}
                                onClick={() => navigate('/approvals')}
                                sx={{
                                    backgroundColor: isActive('/approvals') ? 'rgba(255,255,255,0.1)' : 'transparent'
                                }}
                            >
                                Approvals
                            </Button>
                        )}

                        {hasRole('ROLE_AGENT') && (
                            <Button
                                color="inherit"
                                startIcon={<Work />}
                                onClick={() => navigate('/tasks')}
                                sx={{
                                    backgroundColor: isActive('/tasks') ? 'rgba(255,255,255,0.1)' : 'transparent'
                                }}
                            >
                                My Tasks
                            </Button>
                        )}
                    </Box>

                    <IconButton color="inherit" sx={{ mr: 1 }}>
                        <Badge badgeContent={0} color="error">
                            <Notifications />
                        </Badge>
                    </IconButton>

                    <IconButton
                        size="large"
                        onClick={handleMenu}
                        color="inherit"
                    >
                        <AccountCircle />
                    </IconButton>
                    <Menu
                        anchorEl={anchorEl}
                        open={Boolean(anchorEl)}
                        onClose={handleClose}
                    >
                        <MenuItem disabled>
                            <Typography variant="body2">
                                {user?.username}
                            </Typography>
                        </MenuItem>
                        <MenuItem disabled>
                            <Typography variant="caption" color="text.secondary">
                                {user?.roles?.join(', ')}
                            </Typography>
                        </MenuItem>
                        <MenuItem onClick={handleLogout}>Logout</MenuItem>
                    </Menu>
                </Toolbar>
            </AppBar>

            <Box component="main" sx={{ flexGrow: 1, bgcolor: '#f5f5f5' }}>
                {children}
            </Box>

            <Box component="footer" sx={{ py: 2, px: 3, bgcolor: '#1976d2', color: 'white', textAlign: 'center' }}>
                <Typography variant="body2">
                    Service Request System © 2024 - Enterprise Workflow Automation
                </Typography>
            </Box>
        </Box>
    );
};

export default Layout;
