import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
    Container,
    Box,
    Paper,
    Typography,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TablePagination,
    Chip,
    IconButton,
    TextField,
    MenuItem,
    Button
} from '@mui/material';
import { Visibility, Refresh } from '@mui/icons-material';
import requestService from '../services/requestService';

const statusColors = {
    NEW: 'info',
    PENDING_APPROVAL: 'warning',
    APPROVED: 'success',
    REJECTED: 'error',
    ASSIGNED: 'primary',
    IN_PROGRESS: 'secondary',
    RESOLVED: 'success',
    CLOSED: 'default',
    CANCELLED: 'error'
};

const priorityColors = {
    LOW: 'default',
    MEDIUM: 'info',
    HIGH: 'warning',
    CRITICAL: 'error'
};

const MyRequests = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [requests, setRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [totalElements, setTotalElements] = useState(0);
    const [filterStatus, setFilterStatus] = useState('');

    useEffect(() => {
        loadRequests();
    }, [page, rowsPerPage, filterStatus, location.key]); // Added location.key to force reload

    const loadRequests = async () => {
        setLoading(true);
        console.log('Fetching requests for user...');
        try {
            const response = await requestService.getMyRequests(page, rowsPerPage);
            console.log('MyRequests API response:', response);
            setRequests(response.content || []);
            setTotalElements(response.totalElements || 0);
            console.log('Requests loaded:', response.content?.length || 0);
        } catch (err) {
            console.error('Error loading requests:', err);
            console.error('Error details:', {
                message: err.message,
                response: err.response?.data,
                status: err.response?.status
            });
        } finally {
            setLoading(false);
        }
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const formatDate = (dateString) => {
        if (!dateString) return '-';
        return new Date(dateString).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    return (
        <Container maxWidth="lg">
            <Box sx={{ mt: 4, mb: 4 }}>
                <Paper elevation={3} sx={{ p: 3 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                        <Typography variant="h5">
                            My Service Requests
                        </Typography>
                        <Box>
                            <TextField
                                select
                                size="small"
                                label="Filter by Status"
                                value={filterStatus}
                                onChange={(e) => setFilterStatus(e.target.value)}
                                sx={{ minWidth: 150, mr: 2 }}
                            >
                                <MenuItem value="">All</MenuItem>
                                <MenuItem value="NEW">New</MenuItem>
                                <MenuItem value="PENDING_APPROVAL">Pending Approval</MenuItem>
                                <MenuItem value="IN_PROGRESS">In Progress</MenuItem>
                                <MenuItem value="RESOLVED">Resolved</MenuItem>
                                <MenuItem value="CLOSED">Closed</MenuItem>
                            </TextField>
                            <IconButton onClick={loadRequests} color="primary">
                                <Refresh />
                            </IconButton>
                            <Button
                                variant="contained"
                                onClick={() => navigate('/create-request')}
                                sx={{ ml: 1 }}
                            >
                                New Request
                            </Button>
                        </Box>
                    </Box>

                    <TableContainer>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>Ticket ID</TableCell>
                                    <TableCell>Title</TableCell>
                                    <TableCell>Service</TableCell>
                                    <TableCell>Priority</TableCell>
                                    <TableCell>Status</TableCell>
                                    <TableCell>Created</TableCell>
                                    <TableCell>Actions</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {loading ? (
                                    <TableRow>
                                        <TableCell colSpan={7} align="center">
                                            Loading...
                                        </TableCell>
                                    </TableRow>
                                ) : requests.length === 0 ? (
                                    <TableRow>
                                        <TableCell colSpan={7} align="center">
                                            No requests found. Create your first request!
                                        </TableCell>
                                    </TableRow>
                                ) : (
                                    requests.map((request) => (
                                        <TableRow key={request.id} hover>
                                            <TableCell>
                                                <Typography variant="body2" fontWeight="bold">
                                                    {request.ticketId}
                                                </Typography>
                                            </TableCell>
                                            <TableCell>{request.title}</TableCell>
                                            <TableCell>{request.service?.name || '-'}</TableCell>
                                            <TableCell>
                                                <Chip
                                                    label={request.priority}
                                                    color={priorityColors[request.priority]}
                                                    size="small"
                                                />
                                            </TableCell>
                                            <TableCell>
                                                <Chip
                                                    label={request.status.replace(/_/g, ' ')}
                                                    color={statusColors[request.status]}
                                                    size="small"
                                                />
                                            </TableCell>
                                            <TableCell>{formatDate(request.createdAt)}</TableCell>
                                            <TableCell>
                                                <IconButton
                                                    size="small"
                                                    color="primary"
                                                    onClick={() => navigate(`/request/${request.id}`)}
                                                >
                                                    <Visibility />
                                                </IconButton>
                                            </TableCell>
                                        </TableRow>
                                    ))
                                )}
                            </TableBody>
                        </Table>
                    </TableContainer>

                    <TablePagination
                        component="div"
                        count={totalElements}
                        page={page}
                        onPageChange={handleChangePage}
                        rowsPerPage={rowsPerPage}
                        onRowsPerPageChange={handleChangeRowsPerPage}
                        rowsPerPageOptions={[5, 10, 25, 50]}
                    />
                </Paper>
            </Box>
        </Container>
    );
};

export default MyRequests;
