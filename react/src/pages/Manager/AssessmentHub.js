import { useState, useEffect } from 'react';
import {
    Box, Paper, Button, Stack, Typography, Select, MenuItem,
    FormControl, InputLabel, TextField, List, ListItem, ListItemText,
    Divider, Checkbox, Card, CardContent, CardHeader
} from '@mui/material';
import { useAuth } from '../../AuthContext';
const API_BASE_URL = 'http://localhost:8081';
const DEFAULT_SKILL_LEVEL = 'Beginner';

const SKILL_LEVEL_OPTIONS = [
    { value: 'Beginner', label: 'Beginner' },
    { value: 'Intermediate', label: 'Intermediate' },
    { value: 'Advanced', label: 'Advanced' },
];


export default function AssessmentHubTab() {
    const { currentUser } = useAuth();
    const [templates, setTemplates] = useState([]);
    const [candidates, setCandidates] = useState([]);
    const [selectedTemplateId, setSelectedTemplateId] = useState('');
    const [totalQuestions, setTotalQuestions] = useState('');
    const [selectedCandidateIds, setSelectedCandidateIds] = useState([]);
    const [scheduleDate, setScheduleDate] = useState('');
    const [timeLimit, setTimeLimit] = useState('');
    const [skillLevel, setSkillLevel] = useState(DEFAULT_SKILL_LEVEL);
    const [currentTemplateData, setCurrentTemplateData] = useState(null);
    const [finalScheduleDetails, setFinalScheduleDetails] = useState(null);
    const [showConfirmation, setShowConfirmation] = useState(false);
    const managerId = currentUser?.id;

    useEffect(() => {
        const fetchInitialData = async () => {
            if (!managerId) return;
            try {
                const [templatesRes, candidatesRes] = await Promise.all([
                    fetch(`${API_BASE_URL}/api/adminTemplate/getAllAdminTemplates`),
                    fetch(`${API_BASE_URL}/api/users/managers/${managerId}`),
                ]);
                if (!templatesRes.ok || !candidatesRes.ok) {
                    throw new Error('Failed to fetch initial data from the server.');
                }
                const templatesData = await templatesRes.json();
                const candidatesData = await candidatesRes.json();
                console.log('templatesData :: ', templatesData)
                console.log('candidatesData :: ', candidatesData)
                setTemplates(templatesData);
                setCandidates(candidatesData);
            } catch (error) {
                console.error("Error fetching data:", error);
                alert(error.message);
            }
        };
        fetchInitialData();
    }, [managerId]);

    useEffect(() => {
        const template = templates.find(t => t.adminTemplateId === selectedTemplateId);
        setCurrentTemplateData(template ? { ...template } : null);
    }, [selectedTemplateId, templates]);

    const handleReset = () => {
        setSelectedTemplateId('');
        setSelectedCandidateIds([]);
        setScheduleDate('');
        setTimeLimit('45');
        setSkillLevel(DEFAULT_SKILL_LEVEL);
        setTotalQuestions('15');
        setFinalScheduleDetails(null);
        setShowConfirmation(false);
    };

    const handleSubmitAndSchedule = async () => {
        const scheduledTemplate = templates.find(t => t.adminTemplateId === selectedTemplateId);
        const scheduledCandidates = candidates.filter(c => selectedCandidateIds.includes(c.userId));

        const confirmationDetails = {
            examName: scheduledTemplate ? scheduledTemplate.adminTemplateName : 'N/A',
            candidates: scheduledCandidates,
            scheduleDate: scheduleDate,
        };
        const apiPayload = {
            templateId: selectedTemplateId,
            managerId: managerId,
            questionCount: parseInt(totalQuestions, 10),
            skillLevel: skillLevel,
            userIds: selectedCandidateIds,
            scheduledAt: scheduleDate,
            timeLimitMinutes: timeLimit,
        };

        try {
            const response = await fetch(`${API_BASE_URL}/api/assessment-hub/schedule-exam`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(apiPayload)
            });
            const result = await response.json();
            if (!response.ok) throw new Error(result.message || 'Failed to schedule Assessment');

            console.log("API Response:", result.message);
            setFinalScheduleDetails(confirmationDetails);
            setShowConfirmation(true);
        } catch (error) {
            console.error("Error scheduling Assessment:", error);
        }
    };

    const formatTemplateDetails = () => {
        if (!currentTemplateData) return '';
        const subjectDetails = currentTemplateData.subjects
            .map(s => `• Category: ${s.subject}\n  Weight: ${s.weight}%, Difficulty: ${s.difficulty}`)
            .join('\n\n');
        return `Template Name: ${currentTemplateData.name}\n\n${subjectDetails}`;
    };

    const renderConfirmationPage = () => (
        <Box>
            <Box sx={{ textAlign: 'center', mb: 3 }}>
                <Typography variant="h5" color="success.main" gutterBottom>
                    ✅ Assessment Scheduled Successfully!
                </Typography>
            </Box>
            <Typography variant="h6" sx={{ mb: 1 }}>Confirmation Summary</Typography>
            <Paper variant="outlined" sx={{ p: 3 }}>
                <Stack spacing={2.5}>
                    <Box>
                        <Typography variant="body2" color="text.secondary" gutterBottom>ASSESSMENT NAME</Typography>
                        <Typography variant="h6">{finalScheduleDetails.examName}</Typography>
                    </Box>
                    <Divider />
                    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={{ xs: 2.5, sm: 4 }}>
                        <Box sx={{ flex: 1 }}>
                            <Typography variant="body2" color="text.secondary" gutterBottom>SCHEDULED DATE</Typography>
                            <Typography variant="body1">{new Date(finalScheduleDetails.scheduleDate).toLocaleDateString()}</Typography>
                        </Box>
                        <Box sx={{ flex: 1 }}>
                            <Typography variant="body2" color="text.secondary" gutterBottom>SCHEDULED TIME</Typography>
                            <Typography variant="body1">{new Date(finalScheduleDetails.scheduleDate).toLocaleTimeString()}</Typography>
                        </Box>
                    </Stack>
                    <Divider />
                    <Box>
                        <Typography variant="body2" color="text.secondary" gutterBottom>SCHEDULED CANDIDATES ({finalScheduleDetails.candidates.length})</Typography>
                        <Paper variant='outlined' sx={{ maxHeight: 180, overflow: 'auto' }}>
                            <List dense sx={{ p: 0 }}>
                                {finalScheduleDetails.candidates.map((c, index) => (
                                    <ListItem key={c.userId} divider={index < finalScheduleDetails.candidates.length - 1}>
                                        <ListItemText primary={`${c.firstName} ${c.lastName}`} secondary={c.email} />
                                    </ListItem>
                                ))}
                            </List>
                        </Paper>
                    </Box>
                </Stack>
            </Paper>
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
                <Button variant="contained" color="primary" onClick={handleReset}>
                    Schedule Another Assessment
                </Button>
            </Box>
        </Box>
    );

    const renderFormPage = () => (
        <Box>
            <Typography variant="h6" sx={{ mb: 3 }}>Create New Assessment</Typography>
            <Stack spacing={3}>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
                    <FormControl fullWidth>
                        <InputLabel>Choose Template</InputLabel>
                        <Select
                            value={selectedTemplateId}
                            label="Choose Template"
                            onChange={(e) => setSelectedTemplateId(e.target.value)}
                        >
                            {templates.map((t) => (
                                <MenuItem key={t.adminTemplateId} value={t.adminTemplateId}>{t.adminTemplateName}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    <FormControl fullWidth>
                        <TextField
                            label="Total No of Questions"
                            type="number"
                            value={totalQuestions}
                            onChange={(e) => setTotalQuestions(e.target.value)}
                        />
                    </FormControl>
                </Stack>

                {currentTemplateData && (
                    <Card variant="outlined">
                        <CardHeader title="Template Details" subheader={currentTemplateData.name}
                            sx={{
                                '& .MuiCardHeader-title': {
                                    fontSize: '1rem'
                                },
                                '& .MuiCardHeader-subheader': {
                                    fontSize: '0.8rem'
                                },
                                p: '5px 0 0 10px', '&:last-child': { pb: 0 }
                            }} />
                        <CardContent sx={{ p: '0 5px', '&:last-child': { pb: 0 } }}>
                            <List dense>
                                {currentTemplateData.adminTemplateCategoryMap.map((subj, index) => (
                                    <ListItem key={index} divider>
                                        <ListItemText
                                            primary={subj.category.categoryName}
                                            secondary={`Weight: ${subj.weighage}% | Difficulty: ${subj.difficultyLevel}`}
                                            sx={{
                                                '& .MuiListItemText-primary': {
                                                    fontSize: '.9rem'
                                                },
                                                '& .MuiListItemText-secondary': {
                                                    fontSize: '0.8rem'
                                                }
                                            }}
                                        />
                                    </ListItem>
                                ))}
                            </List>
                        </CardContent>
                    </Card>
                )}

                <Divider sx={{ my: 2 }} />
                <Typography variant="h6">Schedule Details</Typography>

                <FormControl fullWidth>
                    <InputLabel>Select Candidates</InputLabel>
                    <Select
                        multiple
                        value={selectedCandidateIds}
                        label="Select Candidates"
                        onChange={(e) => setSelectedCandidateIds(
                            typeof e.target.value === 'string' ? e.target.value.split(',') : e.target.value,
                        )}
                        renderValue={(selected) =>
                            selected.map(id => {
                                const candidate = candidates.find(c => c.userId === id);
                                return candidate ? `${candidate.firstName} ${candidate.lastName}` : null;
                            })
                                .filter(Boolean).join(', ')
                        }
                        //renderValue={(selected) => selected.map(id => candidates.find(c => c.userId === id)?.fullName).join(', ')}
                    >
                        {candidates.map((c) => (
                            <MenuItem key={c.userId} value={c.userId}>
                                <Checkbox checked={selectedCandidateIds.indexOf(c.userId) > -1} />
                                <ListItemText primary={c.firstName + ' ' + c.lastName} secondary={c.email} />
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>

                <TextField
                    fullWidth
                    label="Scheduled Date & Time"
                    type="datetime-local"
                    value={scheduleDate}
                    onChange={(e) => setScheduleDate(e.target.value)}
                    InputLabelProps={{ shrink: true }}
                    helperText="Note: Valid for 3 working days."
                />

                <FormControl fullWidth>
                    <InputLabel>Assessment Time Limit</InputLabel>
                    <Select value={timeLimit} label="Assessment Time Limit" onChange={(e) => setTimeLimit(e.target.value)}>
                        <MenuItem value="30">30 minutes</MenuItem>
                        <MenuItem value="45">45 minutes</MenuItem>
                        <MenuItem value="60">1 hour</MenuItem>
                        <MenuItem value="90">1 hour 30 minutes</MenuItem>
                    </Select>
                </FormControl>

                <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2 }}>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleSubmitAndSchedule}
                        disabled={!selectedTemplateId || selectedCandidateIds.length === 0 || !scheduleDate}
                    >
                        Submit & Schedule Assessment
                    </Button>
                </Box>
            </Stack>
        </Box>
    );

    return (
        <Paper sx={{ p: 3, mt: 2, border: '1px solid #ddd', borderRadius: 2 }}>
            {showConfirmation && finalScheduleDetails ? renderConfirmationPage() : renderFormPage()}
        </Paper>
    );
}