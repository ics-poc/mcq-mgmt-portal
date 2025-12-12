import { useEffect, useMemo, useState } from 'react';
import {
    Paper, TextField, Button, Stack, Typography, TableContainer, Table, TableHead, TableRow,
    TableCell, TableBody, IconButton, MenuItem, Chip, Box, CircularProgress, Dialog, DialogTitle,
    DialogContent, DialogActions, Menu, InputAdornment, List, ListItem, ListItemText, Divider
} from '@mui/material';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import SearchIcon from '@mui/icons-material/Search';
import EditIcon from '@mui/icons-material/Edit';
import { fetchAPI } from '../../api'; // Adjust path as needed

const initialUserFormState = {
    firstName: '', lastName: '', employeeNumber: '', employeeGrade: '', email: '',
    phone: '', project: '', businessUnit: '', userRoleId: '', roleCode: '', managerId: ''
};
const userRoles = ['Admin', 'Manager', 'Employee'];
const employeeGrades = ['E1', 'E2', 'E3', 'E4', 'E5', 'E6', 'E7', 'E8'];

export default function ManageUsers({ setToast }) {
    const [users, setUsers] = useState([]);
    const [isLoadingUsers, setIsLoadingUsers] = useState(false);
    const [userForm, setUserForm] = useState(initialUserFormState);
    const [managers, setManagers] = useState([]);
    const [isLoadingManagers, setIsLoadingManagers] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const [userAnchorEl, setUserAnchorEl] = useState(null);
    const [selectedUser, setSelectedUser] = useState(null);
    const [userModalMode, setUserModalMode] = useState('create');
    const [isUserFormModalOpen, setUserFormModalOpen] = useState(false);
    const [isUserViewModalOpen, setUserViewModalOpen] = useState(false);

    useEffect(() => {
        const fetchUsers = async () => {
            setIsLoadingUsers(true);
            try {
                const data = await fetchAPI('/api/users');
                setUsers(data);
            } catch (err) {
                setToast(`Failed to load users: ${err.message}`);
            } finally {
                setIsLoadingUsers(false);
            }
        };
        fetchUsers();
    }, [setToast]);

    const filteredUsers = useMemo(() => {
        if (!searchQuery) return users;
        const lowercasedQuery = searchQuery.toLowerCase();
        return users.filter(user =>
            `${user.firstName} ${user.lastName}`.toLowerCase().includes(lowercasedQuery) ||
            (user.employeeNumber || '').toLowerCase().includes(lowercasedQuery) ||
            (user.project || '').toLowerCase().includes(lowercasedQuery) ||
            user.roleCode.toLowerCase().includes(lowercasedQuery) ||
            user.email.toLowerCase().includes(lowercasedQuery)
        );
    }, [users, searchQuery]);

    const canSaveUser = useMemo(() => {
        const {
            firstName, lastName, phone, email, roleCode, managerId,
            employeeNumber, employeeGrade, project, businessUnit
        } = userForm;
        const baseFieldsValid =
            firstName && lastName && phone && email && roleCode &&
            employeeNumber && employeeGrade && project && businessUnit;
        return roleCode === 'Employee' ? baseFieldsValid && managerId : (baseFieldsValid && roleCode !== 'Employee');
    }, [userForm]);

    const handleRoleChange = async (event) => {
        const newRole = event.target.value;
        console.log('newRole :: ', newRole)
        setUserForm({ ...userForm, roleCode: newRole, managerId: '' });
        if (newRole === 'Employee' && managers.length === 0) {
            setIsLoadingManagers(true);
            try {
                const data = await fetchAPI('/api/users/managers');
                setManagers(data);
            } catch (err) {
                setToast(`Failed to fetch managers: ${err.message}`);
            } finally {
                setIsLoadingManagers(false);
            }
        }
    };

    const handleFormSubmit = () => {
        if (userModalMode === 'create') handleCreateUser();
        else handleUpdateUser();
    };

    const handleCreateUser = async () => {
        try {
            const newUser = await fetchAPI('/api/create/user', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(userForm),
            });
            setToast(`User "${newUser.firstName} ${newUser.lastName}" created successfully!`);
            setUsers(prev => [newUser, ...prev]);
            handleUserFormModalClose();
        } catch (err) {
            setToast(`Error: Could not create user. ${err.message}`);
        }
    };

    const handleUpdateUser = async () => {
        try {
            const updatedUser = await fetchAPI(`/api/update/user/${userForm.userId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(userForm),
            });
            setToast(`User "${updatedUser.firstName}" updated successfully!`);
            setUsers(prev => prev.map(u => (u.userId === updatedUser.userId ? updatedUser : u)));
            handleUserFormModalClose();
        } catch (err) {
            setToast(`Error: Could not update user. ${err.message}`);
        }
    };

    const handleDeleteUser = async (userId) => {
        if (!window.confirm('Are you sure you want to delete this user?')) return;
        try {
            await fetchAPI(`/api/users/delete/${userId}`, { method: 'DELETE' });
            setUsers(prev => prev.filter(u => u.userId !== userId));
            setToast('User deleted successfully.');
        } catch (err) {
            setToast(`Error: Could not delete user. ${err.message}`);
        }
        handleUserMenuClose();
    };

    const handleToggleUserStatus = async (userId) => {
        try {
            const updatedUser = await fetchAPI(`/api/users/toggle-status/${userId}`, { method: 'PATCH' });
            setUsers(prev => prev.map(u => (u.userId === userId ? updatedUser : u)));
            setToast(`User status updated to "${updatedUser.status}".`);
        } catch (err) {
            setToast(`Error: Could not update user status. ${err.message}`);
        }
        handleUserMenuClose();
    };

    const handleUserMenuOpen = (event, user) => { setUserAnchorEl(event.currentTarget); setSelectedUser(user); };
    const handleUserMenuClose = () => { setUserAnchorEl(null); };
    const handleUserFormModalClose = () => { setUserFormModalOpen(false); setUserForm(initialUserFormState); setSelectedUser(null); };
    const handleViewModalClose = () => { setUserViewModalOpen(false); setSelectedUser(null); };
    const openCreateModal = () => { setUserModalMode('create'); setUserForm(initialUserFormState); setUserFormModalOpen(true); };
    const openViewModal = () => { setUserViewModalOpen(true); handleUserMenuClose(); };
    const openEditModal = () => { setUserModalMode('edit'); setUserForm(selectedUser); setUserViewModalOpen(false); setUserFormModalOpen(true); handleUserMenuClose(); };

    return (
        <>
            <Paper sx={{ p: 3 }}>
                <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 3 }}>
                    <Button variant="contained" startIcon={<AddCircleOutlineIcon />} onClick={openCreateModal}>
                        Create User
                    </Button>
                    <TextField
                        size="small"
                        placeholder="Search by name, emp no, project..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        InputProps={{
                            startAdornment: (<InputAdornment position="start"><SearchIcon /></InputAdornment>),
                        }}
                        sx={{ width: 320 }}
                    />
                </Stack>
                <TableContainer>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Employee Name</TableCell>
                                <TableCell>Emp No.</TableCell>
                                <TableCell>Project/Program</TableCell>
                                <TableCell>Role</TableCell>
                                <TableCell>Status</TableCell>
                                <TableCell align="right">Actions</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {isLoadingUsers ? (
                                <TableRow><TableCell colSpan={6} align="center"><CircularProgress /></TableCell></TableRow>
                            ) : filteredUsers.length === 0 ? (
                                <TableRow><TableCell colSpan={6} align="center">No users found.</TableCell></TableRow>
                            ) : (
                                filteredUsers.map((user) => (
                                    <TableRow key={user.userId} hover>
                                        <TableCell>
                                            <Typography variant="body1" fontWeight="medium">{`${user.firstName} ${user.lastName}`}</Typography>
                                            <Typography variant="body2" color="text.secondary">{user.email}</Typography>
                                        </TableCell>
                                        <TableCell>{user.employeeNumber}</TableCell>
                                        <TableCell>{user.project}</TableCell>
                                        <TableCell>{user.roleName}</TableCell>
                                        <TableCell>
                                            <Chip
                                                label={user.status}
                                                color={user.status === 'active' ? 'success' : 'default'}
                                                size="small"
                                                variant="outlined"
                                            />
                                        </TableCell>
                                        <TableCell align="right">
                                            <IconButton onClick={(event) => handleUserMenuOpen(event, user)}>
                                                <MoreVertIcon />
                                            </IconButton>
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Paper>

            {/* --- Action Menu --- */}
            <Menu anchorEl={userAnchorEl} open={Boolean(userAnchorEl)} onClose={handleUserMenuClose}>
                <MenuItem onClick={openViewModal}>View</MenuItem>
                <MenuItem onClick={openEditModal}>Edit</MenuItem>
                <MenuItem onClick={() => handleToggleUserStatus(selectedUser.userId)}>
                    {selectedUser?.status === 'active' ? 'Deactivate' : 'Activate'}
                </MenuItem>
                <MenuItem onClick={() => handleDeleteUser(selectedUser.userId)} sx={{ color: 'error.main' }}>
                    Delete
                </MenuItem>
            </Menu>

            {/* --- Updated Create / Edit User Dialog --- */}
            <Dialog open={isUserFormModalOpen} onClose={handleUserFormModalClose} maxWidth="sm" fullWidth>
                <DialogTitle>{userModalMode === 'create' ? 'Create a New User' : 'Edit User Details'}</DialogTitle>
                <DialogContent>
                    <Stack spacing={2} sx={{ mt: 1 }}>
                        <Stack direction="row" spacing={2}>
                            <TextField label="First Name" value={userForm.firstName} onChange={(e) => setUserForm({ ...userForm, firstName: e.target.value })} required fullWidth />
                            <TextField label="Last Name" value={userForm.lastName} onChange={(e) => setUserForm({ ...userForm, lastName: e.target.value })} required fullWidth />
                        </Stack>
                        <Stack direction="row" spacing={2}>
                            <TextField label="Employee Number" value={userForm.employeeNumber} onChange={(e) => setUserForm({ ...userForm, employeeNumber: e.target.value })} required fullWidth />
                            <TextField label="Employee Grade" select value={userForm.employeeGrade} onChange={(e) => setUserForm({ ...userForm, employeeGrade: e.target.value })} required fullWidth>
                                <MenuItem value="" disabled><em>Select a grade</em></MenuItem>
                                {employeeGrades.map(grade => <MenuItem key={grade} value={grade}>{grade}</MenuItem>)}
                            </TextField>
                        </Stack>
                        <TextField label="Mail ID" type="email" value={userForm.email} onChange={(e) => setUserForm({ ...userForm, email: e.target.value })} required />
                        <TextField label="Mobile No" type="tel" value={userForm.phone} onChange={(e) => setUserForm({ ...userForm, phone: e.target.value })} required />
                        <TextField label="Project/Program" value={userForm.project} onChange={(e) => setUserForm({ ...userForm, project: e.target.value })} required />
                        <TextField label="Business Unit" value={userForm.businessUnit} onChange={(e) => setUserForm({ ...userForm, businessUnit: e.target.value })} required />
                        <TextField label="Role" select value={userForm.roleCode} onChange={handleRoleChange} required>
                            <MenuItem value="" disabled><em>Select a role</em></MenuItem>
                            {userRoles.map(roleCode => <MenuItem key={roleCode} value={roleCode}>{roleCode}</MenuItem>)}
                        </TextField>
                        {userForm.roleCode === 'Employee' && (
                            <TextField label="Select Manager" select value={userForm.managerId} onChange={(e) => setUserForm({ ...userForm, managerId: e.target.value })} required disabled={isLoadingManagers}>
                                {isLoadingManagers ? (
                                    <MenuItem value="" disabled><Stack direction="row" alignItems="center" spacing={1}><CircularProgress size={20} /> <Typography>Loading...</Typography></Stack></MenuItem>) : (
                                    managers.map(manager => <MenuItem key={manager.userId} value={manager.userId}>{`${manager.firstName} ${manager.lastName}`}</MenuItem>)
                                )}
                            </TextField>
                        )}
                    </Stack>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleUserFormModalClose}>Cancel</Button>
                    <Button variant="contained" onClick={handleFormSubmit} disabled={!canSaveUser}>
                        {userModalMode === 'create' ? 'Create User' : 'Save Changes'}
                    </Button>
                </DialogActions>
            </Dialog>

            {/* --- Updated View User Details Dialog --- */}
            {selectedUser && (
                <Dialog open={isUserViewModalOpen} onClose={handleViewModalClose} maxWidth="xs" fullWidth>
                    <DialogTitle>User Details</DialogTitle>
                    <DialogContent>
                        <List disablePadding>
                            <ListItem><ListItemText primary="First Name" secondary={selectedUser.firstName} /></ListItem>
                            <Divider component="li" />
                            <ListItem><ListItemText primary="Last Name" secondary={selectedUser.lastName} /></ListItem>
                            <Divider component="li" />
                            <ListItem><ListItemText primary="Employee Number" secondary={selectedUser.employeeNumber || 'N/A'} /></ListItem>
                            <Divider component="li" />
                            <ListItem><ListItemText primary="Employee Grade" secondary={selectedUser.employeeGrade || 'N/A'} /></ListItem>
                            <Divider component="li" />
                            <ListItem><ListItemText primary="Mail ID" secondary={selectedUser.email} /></ListItem>
                            <Divider component="li" />
                            <ListItem><ListItemText primary="Mobile Number" secondary={selectedUser.phone} /></ListItem>
                            <Divider component="li" />
                            <ListItem><ListItemText primary="Project/Program" secondary={selectedUser.project || 'N/A'} /></ListItem>
                            <Divider component="li" />
                            <ListItem><ListItemText primary="Business Unit" secondary={selectedUser.businessUnit || 'N/A'} /></ListItem>
                            <Divider component="li" />
                            <ListItem><ListItemText primary="Role" secondary={selectedUser.roleCode} /></ListItem>
                            <Divider component="li" />
                            <ListItem><ListItemText primary="Status" secondary={<Chip label={selectedUser.status} color={selectedUser.status === 'active' ? 'success' : 'default'} size="small" />} /></ListItem>    </List>
                    </DialogContent>
                    <DialogActions sx={{ justifyContent: 'space-between', px: 3, pb: 2 }}>
                        <Button onClick={handleViewModalClose}>Close</Button>
                        <Button variant="contained" startIcon={<EditIcon />} onClick={openEditModal}>Edit Details</Button>
                    </DialogActions>
                </Dialog>
            )}
        </>
    );
}