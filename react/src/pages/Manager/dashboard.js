import { useState, useMemo, useEffect } from 'react';
import {
  Box, Paper, Typography, Grid, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Chip, Collapse,
  Card, Button, TextField, InputAdornment, MenuItem, Select, FormControl, InputLabel,
  CircularProgress, Alert
} from '@mui/material';
import { useAuth } from '../../AuthContext';
import {
  Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement
} from 'chart.js';
import {
  KeyboardArrowDown as KeyboardArrowDownIcon,
  KeyboardArrowUp as KeyboardArrowUpIcon,
  Search as SearchIcon,
  EventNote as EventNoteIcon,
  CheckCircleOutline as CheckCircleOutlineIcon,
  HourglassEmpty as HourglassEmptyIcon,
  WarningAmber as WarningAmberIcon
} from '@mui/icons-material';


// Register Chart.js components
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement);
function UserRow({ user }) {
  const [open, setOpen] = useState(false);

  const getStatusChipColor = (status) => {
    if (status === 'Pass') return 'success';
    if (status === 'Scheduled') return 'primary';
    if (status === 'Pending') return 'warning';
    if (status === 'Overdue') return 'error';
    if (status === 'Fail') return 'error';
    return 'default';
  };

  return (
    <>
      <TableRow sx={{ '& > *': { borderBottom: 'unset' } }}>
        <TableCell component="th" scope="row">{user.employeeName}</TableCell>
        <TableCell>{user.employeeId}</TableCell>
        <TableCell>{user.employeeGrade}</TableCell>
        <TableCell>{user.project}</TableCell>
        <TableCell>
          <Chip label={user.assessments} size="small" />
        </TableCell>
        <TableCell>
          <Button
            variant="outlined"
            size="small"
            onClick={() => setOpen(!open)}
            endIcon={open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
          >
            View Details
          </Button>
        </TableCell>
      </TableRow>
      <TableRow>
        <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={6}>
          <Collapse in={open} timeout="auto" unmountOnExit>
            <Box sx={{ margin: 1, padding: 2, backgroundColor: '#f9f9f9', borderRadius: '4px' }}>
              <Typography variant="h6" gutterBottom component="div">
                Assessment History
              </Typography>
              <Table size="small" aria-label="assessment history">
                <TableHead>
                  <TableRow>
                    <TableCell>Template Name </TableCell>
                    <TableCell>Category - Weightage</TableCell>
                    <TableCell>Skill Level</TableCell>
                    <TableCell>Date</TableCell>
                    <TableCell>Score</TableCell>
                    <TableCell>Status</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {user.assessmentHistory.map((historyRow) => (
                    <TableRow key={historyRow.userAssessmentTemplateId}>
                      <TableCell>{historyRow.templateName}</TableCell>
                      <TableCell>
                          {historyRow.categories.map((item, index) => (
                            <div key={index}>
                              <strong>{item.category.categoryName}</strong> - ({item.weightage})<br/>
                            </div>
                          ))}
                      </TableCell>
                      <TableCell>Beginner</TableCell>
                      <TableCell>{historyRow.date}{historyRow.isRetake && " (Retake)"}</TableCell>
                      <TableCell>{['Scheduled', 'Pending', 'Overdue'].includes(historyRow.status) ? '-' : historyRow.score}</TableCell>
                      <TableCell>
                        <Chip label={historyRow.status} color={getStatusChipColor(historyRow.status)} size="small" />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </Box>
          </Collapse>
        </TableCell>
      </TableRow>
    </>
  );
}

// --- MAIN COMPONENT: AssessmentDashboard ---
export default function AssessmentDashboard() {
  const { currentUser } = useAuth();
  const [userData, setUserData] = useState([]);
  const managerId = currentUser?.id;
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('All');

  useEffect(() => {
    const fetchData = async () => {
      if (!managerId) {
        setUserData([]);
        setLoading(false);
        return;
      }
      try {
        setLoading(true);
        setError(null);
        const response = await fetch(`http://localhost:8081/api/assessment-dashboard/${managerId}`);
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        setUserData(data);
      } catch (e) {
        setError(e.message);
        console.error("Failed to fetch assessment data:", e);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [managerId]);
  const dashboardStats = useMemo(() => {
    const upcoming = userData.filter(u => u.assessmentStatus === 'Scheduled').length;
    const pending = userData.filter(u => u.assessmentStatus === 'Pending').length;
    const completed = userData.filter(u => u.assessmentStatus === 'Pass').length;
    const overdue = userData.filter(u => u.assessmentStatus === 'Overdue').length;
    return { upcoming, pending, completed, overdue };
  }, [userData]);

  const filteredUsers = useMemo(() => {
    return userData.filter(user => {
      const searchMatch =
        user.employeeName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.employeeId.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.project.toLowerCase().includes(searchTerm.toLowerCase());

      const statusMatch = statusFilter === 'All' || user.assessmentStatus === statusFilter;

      return searchMatch && statusMatch;
    });
  }, [userData, searchTerm, statusFilter]);

  const StatCard = ({ title, value, icon, color, subtitle }) => (
    <Grid item xs={12} sm={6} md={3}>
      <Card sx={{
        p: 3,
        display: 'flex',
        flexDirection: 'column',
        padding: '18px',
        borderRadius: 3,
        width: '221px',
        height: '70%',
        transition: 'transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out',
        '&:hover': {
          transform: 'translateY(-5px)',
          boxShadow: '0 4px 20px 0 rgba(0,0,0,0.12)',
        }
      }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <Typography variant="h4" component="div" sx={{ fontWeight: '400' }}>{value}</Typography>
          <Box sx={{
            width: 48,
            height: 48,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            borderRadius: '12px',
            color: (theme) => theme.palette[color].dark,
            backgroundColor: (theme) => theme.palette[color].light,
            '& .MuiSvgIcon-root': {
              fontSize: '1.75rem',
            },
          }}>
            {icon}
          </Box>
        </Box>
        <Box sx={{ mt: 'auto', pt: 2 }}>
          <Typography sx={{ fontWeight: 'bold' }}>{title}</Typography>
          <Typography color="text.secondary" variant="body2">{subtitle}</Typography>
        </Box>
      </Card>
    </Grid>
  );

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
        <CircularProgress />
        <Typography sx={{ ml: 2 }}>Loading Dashboard...</Typography>
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Failed to load dashboard data. Please make sure the server is running. <br />
          <strong>Error:</strong> {error}
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3, backgroundColor: '#f4f6f8', minHeight: '100vh', mt: 2, borderRadius: 2 }}>

      {/* Section 1: Stat Cards */}
      <Grid container spacing={3} sx={{ mb: 2 }}>
        <StatCard title="Upcoming Assessments" subtitle="All upcoming Assessments" value={dashboardStats.upcoming} icon={<EventNoteIcon />} color="primary" />
        <StatCard title="Pending Assessments" subtitle="Assigned but not started" value={dashboardStats.pending} icon={<HourglassEmptyIcon />} color="warning" />
        <StatCard title="Completed Assessments" subtitle="Successfully completed" value={dashboardStats.completed} icon={<CheckCircleOutlineIcon />} color="success" />
        <StatCard title="Overdue Assessments" subtitle="Missed deadlines" value={dashboardStats.overdue} icon={<WarningAmberIcon />} color="error" />
      </Grid>

      <Paper sx={{ p: 2, borderRadius: '12px' }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2, flexWrap: 'wrap', gap: 2 }}>
          <TextField
            label="Search by Name, ID, Project..."
            variant="outlined"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            sx={{ flexGrow: 1, minWidth: '300px' }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
            }}
          />
          <FormControl variant="outlined" sx={{ minWidth: '200px' }}>
            <InputLabel>Status</InputLabel>
            <Select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              label="Status"
            >
              <MenuItem value="All">All Statuses</MenuItem>
              <MenuItem value="Scheduled">Upcoming</MenuItem>
              <MenuItem value="Pending">Pending</MenuItem>
              <MenuItem value="Pass">Completed</MenuItem>
              <MenuItem value="Overdue">Overdue</MenuItem>
              <MenuItem value="Fail">Failed</MenuItem>
            </Select>
          </FormControl>
        </Box>

        {/* User Table */}
        <TableContainer>
          <Table aria-label="user progress table">
            <TableHead>
              <TableRow sx={{ '& th': { fontWeight: 'bold' } }}>
                <TableCell>Name</TableCell>
                <TableCell>Employee ID</TableCell>
                <TableCell>Employee Grade</TableCell>
                <TableCell>Project / Program</TableCell>
                <TableCell>Assessments</TableCell>
                <TableCell>Action</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredUsers.length > 0 ? (
                filteredUsers.map((user) => (
                  <UserRow key={user.userId} user={user} />
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={6} align="center" sx={{ py: 4 }}>
                    <Typography color="text.secondary">No matching records found.</Typography>
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>
    </Box>
  );
}

