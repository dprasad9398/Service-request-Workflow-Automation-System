import React, { useState, useEffect } from 'react';
import {
    Box,
    Paper,
    Typography,
    Tabs,
    Tab,
    TextField,
    Button,
    Grid,
    CircularProgress,
    Snackbar,
    Alert
} from '@mui/material';
import { Save, Settings as SettingsIcon } from '@mui/icons-material';
import settingsService from '../../services/settingsService';

const SystemSettings = () => {
    const [activeTab, setActiveTab] = useState(0);
    const [loading, setLoading] = useState(false);
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

    // Email settings
    const [emailSettings, setEmailSettings] = useState({
        smtp_host: '',
        smtp_port: '',
        smtp_username: '',
        smtp_password: '',
        from_email: ''
    });

    // SLA settings
    const [slaSettings, setSlaSettings] = useState({
        low_priority_hours: '48',
        medium_priority_hours: '24',
        high_priority_hours: '8',
        critical_priority_hours: '4'
    });

    // Notification settings
    const [notificationSettings, setNotificationSettings] = useState({
        email_notifications: 'true',
        sms_notifications: 'false',
        push_notifications: 'true'
    });

    useEffect(() => {
        loadSettings();
    }, []);

    const loadSettings = async () => {
        setLoading(true);
        try {
            const settings = await settingsService.getAllSettings();

            // Group settings by category
            settings.forEach(setting => {
                if (setting.category === 'EMAIL') {
                    setEmailSettings(prev => ({ ...prev, [setting.settingKey]: setting.settingValue }));
                } else if (setting.category === 'SLA') {
                    setSlaSettings(prev => ({ ...prev, [setting.settingKey]: setting.settingValue }));
                } else if (setting.category === 'NOTIFICATION') {
                    setNotificationSettings(prev => ({ ...prev, [setting.settingKey]: setting.settingValue }));
                }
            });
        } catch (error) {
            showSnackbar('Failed to load settings', 'error');
        } finally {
            setLoading(false);
        }
    };

    const handleSaveEmail = async () => {
        try {
            await settingsService.bulkUpdateSettings('EMAIL', emailSettings);
            showSnackbar('Email settings saved successfully');
        } catch (error) {
            showSnackbar('Failed to save email settings', 'error');
        }
    };

    const handleSaveSLA = async () => {
        try {
            await settingsService.bulkUpdateSettings('SLA', slaSettings);
            showSnackbar('SLA settings saved successfully');
        } catch (error) {
            showSnackbar('Failed to save SLA settings', 'error');
        }
    };

    const handleSaveNotifications = async () => {
        try {
            await settingsService.bulkUpdateSettings('NOTIFICATION', notificationSettings);
            showSnackbar('Notification settings saved successfully');
        } catch (error) {
            showSnackbar('Failed to save notification settings', 'error');
        }
    };

    const showSnackbar = (message, severity = 'success') => {
        setSnackbar({ open: true, message, severity });
    };

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <SettingsIcon sx={{ mr: 2, fontSize: 32 }} />
                <Typography variant="h4">System Settings</Typography>
            </Box>

            <Paper>
                <Tabs value={activeTab} onChange={(e, v) => setActiveTab(v)}>
                    <Tab label="Email Configuration" />
                    <Tab label="SLA Settings" />
                    <Tab label="Notifications" />
                </Tabs>

                <Box sx={{ p: 3 }}>
                    {/* Email Settings Tab */}
                    {activeTab === 0 && (
                        <Grid container spacing={3}>
                            <Grid item xs={12}>
                                <Typography variant="h6" gutterBottom>Email Server Configuration</Typography>
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    fullWidth
                                    label="SMTP Host"
                                    value={emailSettings.smtp_host}
                                    onChange={(e) => setEmailSettings({ ...emailSettings, smtp_host: e.target.value })}
                                />
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    fullWidth
                                    label="SMTP Port"
                                    value={emailSettings.smtp_port}
                                    onChange={(e) => setEmailSettings({ ...emailSettings, smtp_port: e.target.value })}
                                />
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    fullWidth
                                    label="Username"
                                    value={emailSettings.smtp_username}
                                    onChange={(e) => setEmailSettings({ ...emailSettings, smtp_username: e.target.value })}
                                />
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    fullWidth
                                    type="password"
                                    label="Password"
                                    value={emailSettings.smtp_password}
                                    onChange={(e) => setEmailSettings({ ...emailSettings, smtp_password: e.target.value })}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    fullWidth
                                    label="From Email"
                                    value={emailSettings.from_email}
                                    onChange={(e) => setEmailSettings({ ...emailSettings, from_email: e.target.value })}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <Button variant="contained" startIcon={<Save />} onClick={handleSaveEmail}>
                                    Save Email Settings
                                </Button>
                            </Grid>
                        </Grid>
                    )}

                    {/* SLA Settings Tab */}
                    {activeTab === 1 && (
                        <Grid container spacing={3}>
                            <Grid item xs={12}>
                                <Typography variant="h6" gutterBottom>SLA Response Time (Hours)</Typography>
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    fullWidth
                                    type="number"
                                    label="Low Priority"
                                    value={slaSettings.low_priority_hours}
                                    onChange={(e) => setSlaSettings({ ...slaSettings, low_priority_hours: e.target.value })}
                                />
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    fullWidth
                                    type="number"
                                    label="Medium Priority"
                                    value={slaSettings.medium_priority_hours}
                                    onChange={(e) => setSlaSettings({ ...slaSettings, medium_priority_hours: e.target.value })}
                                />
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    fullWidth
                                    type="number"
                                    label="High Priority"
                                    value={slaSettings.high_priority_hours}
                                    onChange={(e) => setSlaSettings({ ...slaSettings, high_priority_hours: e.target.value })}
                                />
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <TextField
                                    fullWidth
                                    type="number"
                                    label="Critical Priority"
                                    value={slaSettings.critical_priority_hours}
                                    onChange={(e) => setSlaSettings({ ...slaSettings, critical_priority_hours: e.target.value })}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <Button variant="contained" startIcon={<Save />} onClick={handleSaveSLA}>
                                    Save SLA Settings
                                </Button>
                            </Grid>
                        </Grid>
                    )}

                    {/* Notification Settings Tab */}
                    {activeTab === 2 && (
                        <Grid container spacing={3}>
                            <Grid item xs={12}>
                                <Typography variant="h6" gutterBottom>Notification Preferences</Typography>
                            </Grid>
                            <Grid item xs={12}>
                                <Typography>Email Notifications: {notificationSettings.email_notifications}</Typography>
                                <Typography>SMS Notifications: {notificationSettings.sms_notifications}</Typography>
                                <Typography>Push Notifications: {notificationSettings.push_notifications}</Typography>
                            </Grid>
                            <Grid item xs={12}>
                                <Button variant="contained" startIcon={<Save />} onClick={handleSaveNotifications}>
                                    Save Notification Settings
                                </Button>
                            </Grid>
                        </Grid>
                    )}
                </Box>
            </Paper>

            <Snackbar
                open={snackbar.open}
                autoHideDuration={6000}
                onClose={() => setSnackbar({ ...snackbar, open: false })}
            >
                <Alert severity={snackbar.severity}>{snackbar.message}</Alert>
            </Snackbar>
        </Box>
    );
};

export default SystemSettings;
