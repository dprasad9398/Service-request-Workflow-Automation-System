import React, { useState, useEffect } from 'react';
import {
    Container,
    Box,
    Typography,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TablePagination,
    Button,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Chip,
    IconButton,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Alert,
    Grid
} from '@mui/material';
import {
    Assignment as AssignmentIcon,
    Person as PersonIcon,
    Update as UpdateIcon,
    Timeline as TimelineIcon,
    Refresh as RefreshIcon
} from '@mui/icons-material';
import adminRequestService from '../../services/adminRequestService';
import departmentService from '../../services/departmentService';

/**
 * Admin Manage Requests Page
 * Allows admin to view, filter, and manage all service requests
 */
const AdminManageRequests = () => {
    // State for requests and pagination
    const [requests, setRequests] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [totalItems, setTotalItems] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    // State for filters
    const [filters, setFilters] = useState({
        category: 'ALL',
        department: 'ALL',
        priority: 'ALL',
        status: 'ALL'
    });

    // State for departments and agents
    const [departments, setDepartments] = useState([]);
    const [agents, setAgents] = useState([]);

    // State for modals
    const [assignDeptModal, setAssignDeptModal] = useState({ open: false, requestId: null });
    const [assignAgentModal, setAssignAgentModal] = useState({ open: false, requestId: null });
    const [updateStatusModal, setUpdateStatusModal] = useState({ open: false, requestId: null });
    const [timelineModal, setTimelineModal] = useState({ open: false, requestId: null, timeline: [] });

    // State for form inputs
    const [selectedDepartment, setSelectedDepartment] = useState('');
    const [selectedAgent, setSelectedAgent] = useState('');
    const [selectedStatus, setSelectedStatus] = useState('');
    const [notes, setNotes] = useState('');

    useEffect(() => {
        loadRequests();
        loadDepartments();
        loadAgents();
    }, [page, rowsPerPage, filters]);

    const loadRequests = async () => {
        setLoading(true);
        setError('');
        try {
            const filterParams = {};
            if (filters.category !== 'ALL') filterParams.category = filters.category;
            if (filters.department !== 'ALL') filterParams.department = filters.department;
            if (filters.priority !== 'ALL') filterParams.priority = filters.priority;
            if (filters.status !== 'ALL') filterParams.status = filters.status;

            const data = await adminRequestService.getAllRequests(filterParams, page, rowsPerPage);
            setRequests(data.requests);
            setTotalItems(data.totalItems);
        } catch (err) {
            setError('Failed to load requests: ' + err.message);
        } finally {
            setLoading(false);
        }
    };

    const loadDepartments = async () => {
        try {
            const data = await departmentService.getAllDepartments();
            setDepartments(data);
        } catch (err) {
            console.error('Failed to load departments:', err);
        }
    };

    const loadAgents = async () => {
        try {
            const data = await departmentService.getAllAgents();
            setAgents(data);
        } catch (err) {
            console.error('Failed to load agents:', err);
        }
    };

    const handleFilterChange = (field, value) => {
        setFilters({ ...filters, [field]: value });
        setPage(0); // Reset to first page when filter changes
    };

    const handleAssignDepartment = async () => {
        try {
            await adminRequestService.assignDepartment(assignDeptModal.requestId, selectedDepartment, notes);
            setSuccess('Department assigned successfully');
            setAssignDeptModal({ open: false, requestId: null });
            setSelectedDepartment('');
            setNotes('');
            loadRequests();
        } catch (err) {
            setError('Failed to assign department: ' + err.message);
        }
    };

    const handleAssignAgent = async () => {
        try {
            await adminRequestService.assignAgent(assignAgentModal.requestId, selectedAgent, notes);
            setSuccess('Agent assigned successfully');
            setAssignAgentModal({ open: false, requestId: null });
            setSelectedAgent('');
            setNotes('');
            loadRequests();
        } catch (err) {
            setError('Failed to assign agent: ' + err.message);
        }
    };

    const handleUpdateStatus = async () => {
        try {
            await adminRequestService.updateStatus(updateStatusModal.requestId, selectedStatus, notes);
            setSuccess('Status updated successfully');
            setUpdateStatusModal({ open: false, requestId: null });
            setSelectedStatus('');
            setNotes('');
            loadRequests();
        } catch (err) {
            setError('Failed to update status: ' + err.message);
        }
    };

    const handleViewTimeline = async (requestId) => {
        try {
            const timeline = await adminRequestService.getTimeline(requestId);
            setTimelineModal({ open: true, requestId, timeline });
        } catch (err) {
            setError('Failed to load timeline: ' + err.message);
        }
    };

    const getPriorityColor = (priority) => {
        switch (priority) {
            case 'CRITICAL': return 'error';
            case 'HIGH': return 'warning';
            case 'MEDIUM': return 'info';
            case 'LOW': return 'success';
            default: return 'default';
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'NEW': return 'info';
            case 'ASSIGNED': return 'primary';
            case 'IN_PROGRESS': return 'warning';
            case 'RESOLVED': return 'success';
            case 'CLOSED': return 'default';
            default: return 'default';
        }
    };

    return (
        <Container maxWidth="xl">
            <Box sx={{ mt: 4, mb: 4 }}>
                <Typography variant="h4" gutterBottom>
                    Manage Service Requests
                </Typography>

                {error && (
                    <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>
                        {error}
                    </Alert>
                )}

                {success && (
                    <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>
                        {success}
                    </Alert>
                )}

                {/* Filters */}
                <Paper sx={{ p: 2, mb: 3 }}>
                    <Typography variant="h6" gutterBottom>
                        Filters
                    </Typography>
                    <Grid container spacing={2}>
                        <Grid item xs={12} sm={6} md={3}>
                            <FormControl fullWidth size="small">
                                <InputLabel>Category</InputLabel>
                                <Select
                                    value={filters.category}
                                    onChange={(e) => handleFilterChange('category', e.target.value)}
                                    label="Category"
                                >
                                    <MenuItem value="ALL">All Categories</MenuItem>
                                    <MenuItem value="IT Support">IT Support</MenuItem>
                                    <MenuItem value="HR Requests">HR Requests</MenuItem>
                                    <MenuItem value="Facilities">Facilities</MenuItem>
                                    <MenuItem value="General">General</MenuItem>
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={6} md={3}>
                            <FormControl fullWidth size="small">
                                <InputLabel>Department</InputLabel>
                                <Select
                                    value={filters.department}
                                    onChange={(e) => handleFilterChange('department', e.target.value)}
                                    label="Department"
                                >
                                    <MenuItem value="ALL">All Departments</MenuItem>
                                    {departments.map((dept) => (
                                        <MenuItem key={dept.id} value={dept.name}>{dept.name}</MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={6} md={3}>
                            <FormControl fullWidth size="small">
                                <InputLabel>Priority</InputLabel>
                                <Select
                                    value={filters.priority}
                                    onChange={(e) => handleFilterChange('priority', e.target.value)}
                                    label="Priority"
                                >
                                    <MenuItem value="ALL">All Priorities</MenuItem>
                                    <MenuItem value="CRITICAL">Critical</MenuItem>
                                    <MenuItem value="HIGH">High</MenuItem>
                                    <MenuItem value="MEDIUM">Medium</MenuItem>
                                    <MenuItem value="LOW">Low</MenuItem>
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={6} md={3}>
                            <FormControl fullWidth size="small">
                                <InputLabel>Status</InputLabel>
                                <Select
                                    value={filters.status}
                                    onChange={(e) => handleFilterChange('status', e.target.value)}
                                    label="Status"
                                >
                                    <MenuItem value="ALL">All Statuses</MenuItem>
                                    <MenuItem value="NEW">New</MenuItem>
                                    <MenuItem value="ASSIGNED">Assigned</MenuItem>
                                    <MenuItem value="IN_PROGRESS">In Progress</MenuItem>
                                    <MenuItem value="RESOLVED">Resolved</MenuItem>
                                    <MenuItem value="CLOSED">Closed</MenuItem>
                                </Select>
                            </FormControl>
                        </Grid>
                    </Grid>
                    <Box sx={{ mt: 2, display: 'flex', gap: 1 }}>
                        <Button
                            variant="outlined"
                            startIcon={<RefreshIcon />}
                            onClick={loadRequests}
                        >
                            Refresh
                        </Button>
                    </Box>
                </Paper>

                {/* Requests Table */}
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Ticket ID</TableCell>
                                <TableCell>User</TableCell>
                                <TableCell>Category</TableCell>
                                <TableCell>Title</TableCell>
                                <TableCell>Priority</TableCell>
                                <TableCell>Status</TableCell>
                                <TableCell>Department</TableCell>
                                <TableCell>Agent</TableCell>
                                <TableCell>Created</TableCell>
                                <TableCell>Actions</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {loading ? (
                                <TableRow>
                                    <TableCell colSpan={10} align="center">
                                        Loading...
                                    </TableCell>
                                </TableRow>
                            ) : requests.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={10} align="center">
                                        No requests found
                                    </TableCell>
                                </TableRow>
                            ) : (
                                requests.map((request) => (
                                    <TableRow key={request.id}>
                                        <TableCell>{request.ticketId}</TableCell>
                                        <TableCell>{request.userName}</TableCell>
                                        <TableCell>{request.categoryName}</TableCell>
                                        <TableCell>{request.title}</TableCell>
                                        <TableCell>
                                            <Chip
                                                label={request.priority}
                                                color={getPriorityColor(request.priority)}
                                                size="small"
                                            />
                                        </TableCell>
                                        <TableCell>
                                            <Chip
                                                label={request.status}
                                                color={getStatusColor(request.status)}
                                                size="small"
                                            />
                                        </TableCell>
                                        <TableCell>{request.departmentName}</TableCell>
                                        <TableCell>{request.assignedAgentName}</TableCell>
                                        <TableCell>
                                            {new Date(request.createdAt).toLocaleDateString()}
                                        </TableCell>
                                        <TableCell>
                                            <Box sx={{ display: 'flex', gap: 0.5 }}>
                                                <IconButton
                                                    size="small"
                                                    color="primary"
                                                    onClick={() => setAssignDeptModal({ open: true, requestId: request.id })}
                                                    title="Assign Department"
                                                >
                                                    <AssignmentIcon fontSize="small" />
                                                </IconButton>
                                                <IconButton
                                                    size="small"
                                                    color="secondary"
                                                    onClick={() => setAssignAgentModal({ open: true, requestId: request.id })}
                                                    title="Assign Agent"
                                                >
                                                    <PersonIcon fontSize="small" />
                                                </IconButton>
                                                <IconButton
                                                    size="small"
                                                    color="info"
                                                    onClick={() => setUpdateStatusModal({ open: true, requestId: request.id })}
                                                    title="Update Status"
                                                >
                                                    <UpdateIcon fontSize="small" />
                                                </IconButton>
                                                <IconButton
                                                    size="small"
                                                    onClick={() => handleViewTimeline(request.id)}
                                                    title="View Timeline"
                                                >
                                                    <TimelineIcon fontSize="small" />
                                                </IconButton>
                                            </Box>
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                    <TablePagination
                        component="div"
                        count={totalItems}
                        page={page}
                        onPageChange={(e, newPage) => setPage(newPage)}
                        rowsPerPage={rowsPerPage}
                        onRowsPerPageChange={(e) => {
                            setRowsPerPage(parseInt(e.target.value, 10));
                            setPage(0);
                        }}
                    />
                </TableContainer>

                {/* Assign Department Modal */}
                <Dialog open={assignDeptModal.open} onClose={() => setAssignDeptModal({ open: false, requestId: null })}>
                    <DialogTitle>Assign Department</DialogTitle>
                    <DialogContent>
                        <FormControl fullWidth sx={{ mt: 2 }}>
                            <InputLabel>Department</InputLabel>
                            <Select
                                value={selectedDepartment}
                                onChange={(e) => setSelectedDepartment(e.target.value)}
                                label="Department"
                            >
                                {departments.map((dept) => (
                                    <MenuItem key={dept.id} value={dept.id}>{dept.name}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                        <TextField
                            fullWidth
                            label="Notes"
                            multiline
                            rows={3}
                            value={notes}
                            onChange={(e) => setNotes(e.target.value)}
                            sx={{ mt: 2 }}
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setAssignDeptModal({ open: false, requestId: null })}>Cancel</Button>
                        <Button onClick={handleAssignDepartment} variant="contained">Assign</Button>
                    </DialogActions>
                </Dialog>

                {/* Assign Agent Modal */}
                <Dialog open={assignAgentModal.open} onClose={() => setAssignAgentModal({ open: false, requestId: null })}>
                    <DialogTitle>Assign Agent</DialogTitle>
                    <DialogContent>
                        <FormControl fullWidth sx={{ mt: 2 }}>
                            <InputLabel>Agent</InputLabel>
                            <Select
                                value={selectedAgent}
                                onChange={(e) => setSelectedAgent(e.target.value)}
                                label="Agent"
                            >
                                {agents.map((agent) => (
                                    <MenuItem key={agent.id} value={agent.id}>
                                        {agent.username} ({agent.email})
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                        <TextField
                            fullWidth
                            label="Notes"
                            multiline
                            rows={3}
                            value={notes}
                            onChange={(e) => setNotes(e.target.value)}
                            sx={{ mt: 2 }}
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setAssignAgentModal({ open: false, requestId: null })}>Cancel</Button>
                        <Button onClick={handleAssignAgent} variant="contained">Assign</Button>
                    </DialogActions>
                </Dialog>

                {/* Update Status Modal */}
                <Dialog open={updateStatusModal.open} onClose={() => setUpdateStatusModal({ open: false, requestId: null })}>
                    <DialogTitle>Update Status</DialogTitle>
                    <DialogContent>
                        <FormControl fullWidth sx={{ mt: 2 }}>
                            <InputLabel>Status</InputLabel>
                            <Select
                                value={selectedStatus}
                                onChange={(e) => setSelectedStatus(e.target.value)}
                                label="Status"
                            >
                                <MenuItem value="NEW">New</MenuItem>
                                <MenuItem value="ASSIGNED">Assigned</MenuItem>
                                <MenuItem value="IN_PROGRESS">In Progress</MenuItem>
                                <MenuItem value="RESOLVED">Resolved</MenuItem>
                                <MenuItem value="CLOSED">Closed</MenuItem>
                            </Select>
                        </FormControl>
                        <TextField
                            fullWidth
                            label="Notes"
                            multiline
                            rows={3}
                            value={notes}
                            onChange={(e) => setNotes(e.target.value)}
                            sx={{ mt: 2 }}
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setUpdateStatusModal({ open: false, requestId: null })}>Cancel</Button>
                        <Button onClick={handleUpdateStatus} variant="contained">Update</Button>
                    </DialogActions>
                </Dialog>

                {/* Timeline Modal */}
                <Dialog
                    open={timelineModal.open}
                    onClose={() => setTimelineModal({ open: false, requestId: null, timeline: [] })}
                    maxWidth="md"
                    fullWidth
                >
                    <DialogTitle>Request Timeline</DialogTitle>
                    <DialogContent>
                        {timelineModal.timeline.length === 0 ? (
                            <Typography>No timeline data available</Typography>
                        ) : (
                            <Box>
                                {timelineModal.timeline.map((entry, index) => (
                                    <Box key={index} sx={{ mb: 2, p: 2, border: '1px solid #ddd', borderRadius: 1 }}>
                                        <Typography variant="subtitle2">
                                            {entry.oldStatus} → {entry.newStatus}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            {new Date(entry.changedAt).toLocaleString()}
                                        </Typography>
                                        {entry.notes && (
                                            <Typography variant="body2" sx={{ mt: 1 }}>
                                                {entry.notes}
                                            </Typography>
                                        )}
                                    </Box>
                                ))}
                            </Box>
                        )}
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setTimelineModal({ open: false, requestId: null, timeline: [] })}>
                            Close
                        </Button>
                    </DialogActions>
                </Dialog>
            </Box>
        </Container>
    );
};

export default AdminManageRequests;
