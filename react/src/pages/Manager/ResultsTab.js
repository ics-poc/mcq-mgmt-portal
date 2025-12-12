import { useState, useMemo, useEffect } from 'react';
import {
  Paper, Typography, TableContainer, FormControl, InputLabel, Select,
  MenuItem, Table, TableHead, TableRow, TableCell, TableBody, Button,
  Dialog, DialogTitle, DialogContent, Box, Divider, TextField,
  InputAdornment, Stack, Grid, Accordion, AccordionSummary, AccordionDetails,
  LinearProgress, Chip, DialogActions, CircularProgress
} from '@mui/material';
import { Search, ExpandMore, Person, Email, CalendarToday } from '@mui/icons-material';
import { useAuth } from '../../AuthContext';


const getScoreColor = (score) => {
  if (score >= 90) return 'success';
  if (score >= 75) return 'primary';
  if (score >= 60) return 'warning';
  return 'error';
};

const calculateAverageScore = (skillLevels) => {
  let totalScore = 0;
  let subjectCount = 0;
  Object.values(skillLevels).forEach(level => {
    Object.values(level.subjects).forEach(subject => {
      totalScore += subject.score;
      subjectCount++;
    });
  });
  return subjectCount > 0 ? (totalScore / subjectCount) : 0;
};

export default function ResultsTab() {
  const { currentUser } = useAuth();
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [open, setOpen] = useState(false);
  const [detail, setDetail] = useState(null);
  const [subjectFilter, setSubjectFilter] = useState('All');
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    const fetchResults = async () => {
      try {
        const response = await fetch('http://localhost:3001/api/results');
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        const data = await response.json();
        setResults(data);
      } catch (e) {
        setError(e.message);
        console.error("Failed to fetch results:", e);
      } finally {
        setLoading(false);
      }
    };

    fetchResults();
  }, []);
  const subjectOptions = useMemo(() => {
    const allSubjects = new Set(['All']);
    results.forEach(result => {
      Object.values(result.skillLevels).forEach(level => {
        Object.keys(level.subjects).forEach(subject => allSubjects.add(subject));
      });
    });
    return Array.from(allSubjects);
  }, [results]);

  const tableResults = useMemo(() => {
    return results
      .filter(r =>
        r.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        r.email.toLowerCase().includes(searchQuery.toLowerCase())
      )
      .filter(r => {
        if (subjectFilter === 'All') return true;
        return Object.values(r.skillLevels).some(level => level.subjects[subjectFilter]);
      });
  }, [results, searchQuery, subjectFilter]);

  const handleOpenDetails = (result) => {
    setDetail(result);
    setOpen(true);
  };
  const handleCloseDetails = () => {
    setOpen(false);
  };

  if (loading) {
    return (
      <Paper sx={{ p: 3, mt: 2, textAlign: 'center' }}>
        <CircularProgress />
        <Typography>Loading results...</Typography>
      </Paper>
    );
  }
  if (error) {
    return (
      <Paper sx={{ p: 3, mt: 2, textAlign: 'center' }}>
        <Typography color="error">Error: {error}</Typography>
        <Typography>Could not fetch data from the server. Please ensure the backend is running.</Typography>
      </Paper>
    );
  }

  return (
    <>
      <Paper sx={{ p: 3, mt: 2 }}>
        <Typography variant="h6">Candidate Results</Typography>
        <Box sx={{ display: 'flex', gap: 2, my: 2 }}>
          <TextField
            fullWidth label="Search by Name or Email" variant="outlined" size="small"
            value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)}
            InputProps={{ startAdornment: (<InputAdornment position="start"><Search /></InputAdornment>), }}
          />
          <FormControl sx={{ minWidth: 200 }} size="small">
            <InputLabel id="subject-filter-label">Filter by Subject</InputLabel>
            <Select
              labelId="subject-filter-label" label="Filter by Subject" value={subjectFilter}
              onChange={(e) => setSubjectFilter(e.target.value)}
            >
              {subjectOptions.map((subject) => (
                <MenuItem key={subject} value={subject}>{subject}</MenuItem>
              ))}
            </Select>
          </FormControl>
        </Box>
        <TableContainer>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>User Name</TableCell>
                <TableCell>Email</TableCell>
                <TableCell>Average Score</TableCell>
                <TableCell>Submitted At</TableCell>
                <TableCell>Action</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {tableResults.map((r, i) => (
                <TableRow key={i}>
                  <TableCell>{r.name}</TableCell>
                  <TableCell>{r.email}</TableCell>
                  <TableCell>{calculateAverageScore(r.skillLevels).toFixed(2)}</TableCell>
                  <TableCell>{new Date(r.submittedAt).toLocaleString()}</TableCell>
                  <TableCell>
                    <Button variant="outlined" size="small" onClick={() => handleOpenDetails(r)}>View</Button>
                  </TableCell>
                </TableRow>
              ))}
              {tableResults.length === 0 && (
                <TableRow><TableCell colSpan={5} align="center">No results found</TableCell></TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>

      <Dialog open={open} onClose={handleCloseDetails} maxWidth="md" fullWidth>
        <DialogTitle sx={{ m: 0, p: 2, borderBottom: '1px solid #ddd' }}>
          Candidate Score Report
        </DialogTitle>
        <DialogContent sx={{ p: 3, bgcolor: '#f7f9fc' }}>
          {detail && (
            <Grid container spacing={2}>
              <Grid item xs={12} md={4}>
                <Paper elevation={2} sx={{ p: 2, height: '100%' }}>
                  <Stack spacing={2}>
                    <Typography variant="h6" component="div">{detail.name}</Typography>
                    <Divider />
                    <Stack direction="row" spacing={1} alignItems="center">
                      <Email color="action" /><Typography variant="body2">{detail.email}</Typography>
                    </Stack>
                    <Stack direction="row" spacing={1} alignItems="center">
                      <CalendarToday color="action" /><Typography variant="body2">{new Date(detail.submittedAt).toLocaleString()}</Typography>
                    </Stack>
                    <Divider />
                    <Box sx={{ textAlign: 'center', py: 2 }}>
                      <Typography variant="overline">Overall Average</Typography>
                      <Typography variant="h4" color="primary">{calculateAverageScore(detail.skillLevels).toFixed(2)}</Typography>
                    </Box>
                  </Stack>
                </Paper>
              </Grid>
              <Grid item xs={12} md={8}>
                {Object.entries(detail.skillLevels).map(([level, levelData], index) => (
                  <Accordion defaultExpanded={index === 0} key={level}>
                    <AccordionSummary expandIcon={<ExpandMore />}><Typography sx={{ fontWeight: 'bold' }}>{level}</Typography></AccordionSummary>
                    <AccordionDetails sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                      {Object.entries(levelData.subjects).map(([subject, subjectData]) => (
                        <Box key={subject}>
                          <Stack direction="row" justifyContent="space-between" alignItems="center">
                            <Typography>{subject}</Typography>
                            <Chip label={subjectData.score} color={getScoreColor(subjectData.score)} size="small" />
                          </Stack>
                          <LinearProgress variant="determinate" value={subjectData.score} color={getScoreColor(subjectData.score)} sx={{ height: 6, borderRadius: 5, mt: 0.5 }} />
                        </Box>
                      ))}
                    </AccordionDetails>
                  </Accordion>
                ))}
              </Grid>
            </Grid>
          )}
        </DialogContent>
        <DialogActions sx={{ p: '8px 24px' }}>
          <Button onClick={handleCloseDetails} variant="contained">Close</Button>
        </DialogActions>
      </Dialog>
    </>
  );
}