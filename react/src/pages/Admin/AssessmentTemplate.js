import { useEffect, useMemo, useState } from 'react';
import {
    Paper, TextField, Button, Stack, Typography, TableContainer, Table, TableHead, TableRow,
    TableCell, TableBody, IconButton, MenuItem, Chip, Box, CircularProgress, Dialog, DialogTitle,
    DialogContent, DialogActions, Menu, List, ListItem, ListItemText, Divider, Alert
} from '@mui/material';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import DeleteIcon from '@mui/icons-material/Delete';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import EditIcon from '@mui/icons-material/Edit';
import { fetchAPI } from '../../api'; // Adjust path as needed

const initialTemplateSubject = { id: crypto.randomUUID(), subject: '', weight: '', difficulty: '' };
const initialTemplateFormState = { name: '', description: '', subjects: [initialTemplateSubject] };
const availableDifficulties = ['Easy', 'Medium', 'Hard', 'Difficult', 'Beginner'];

export default function AssessmentTemplate({ setToast }) {
    const [templateForm, setTemplateForm] = useState(initialTemplateFormState);
    const [savedTemplates, setSavedTemplates] = useState([]);
    const [availableSubjects, setAvailableSubjects] = useState([]);
    const [isLoadingTemplates, setIsLoadingTemplates] = useState(false);
    const [templateError, setTemplateError] = useState(null);
    const [templateAnchorEl, setTemplateAnchorEl] = useState(null);
    const [selectedTemplate, setSelectedTemplate] = useState(null);
    const [templateModalMode, setTemplateModalMode] = useState('create');
    const [isTemplateFormModalOpen, setTemplateFormModalOpen] = useState(false);
    const [isTemplateViewModalOpen, setTemplateViewModalOpen] = useState(false);

    useEffect(() => {
        const fetchTemplateData = async () => {
            setIsLoadingTemplates(true);
            setTemplateError(null);
            try {
                const [subjectsData, templatesData] = await Promise.all([
                    fetchAPI('/api/categories/getAllCategories'),
                    fetchAPI('/api/adminTemplate/getAllAdminTemplates')
                ]);
                const uniqueCategories = templatesData.length > 0 ?
                    availableSubjects : // If templates exist, API might be different (keep old logic)
                    [...new Map(subjectsData.map(item => [item.categoryName, item])).values()]; // Use new API data
                setAvailableSubjects(uniqueCategories);
                setSavedTemplates(templatesData);
            } catch (err) {
                setTemplateError(`Failed to load template data: ${err.message}`);
            } finally {
                setIsLoadingTemplates(false);
            }
        };
        fetchTemplateData();
    }, [availableSubjects]); // Keep availableSubjects dependency as in original

    const totalWeight = useMemo(() => templateForm.subjects.reduce((sum, s) => sum + (Number(s.weight) || 0), 0), [templateForm.subjects]);

    const handleSubjectChange = (id, field, value) => {
        const updatedSubjects = templateForm.subjects.map(s => (s.id === id ? { ...s, [field]: value } : s));
        setTemplateForm(prev => ({ ...prev, subjects: updatedSubjects }));
    };

    const addSubjectRow = () => { setTemplateForm(prev => ({ ...prev, subjects: [...prev.subjects, { ...initialTemplateSubject, id: crypto.randomUUID() }] })); };

    const removeSubjectRow = (id) => { setTemplateForm(prev => ({ ...prev, subjects: prev.subjects.filter(s => s.id !== id) })); };

    const canCreateTemplate = useMemo(() => {
        const { name, subjects } = templateForm;
        const hasName = name.trim() !== '';
        const isWeight100 = totalWeight === 100;
        const allFieldsFilled = subjects.every(s => s.subject && s.weight && s.difficulty);
        return hasName && isWeight100 && allFieldsFilled;
    }, [templateForm, totalWeight]);

    const handleTemplateFormSubmit = () => {
        if (templateModalMode === 'create') handleCreateTemplate(); else handleUpdateTemplate();
    };

    const handleCreateTemplate = async () => {
        const categoryIdLookup = availableSubjects.reduce((acc, sub) => {
            acc[sub.categoryName] = sub.categoryId;
            return acc;
        }, {});

        const payload = {
            adminTemplateName: templateForm.name,
            description: templateForm.description,
            createdUserId: "ADMIN", // Or use value from useAuth() if available
            adminTemplateCategoryMap: templateForm.subjects.map(s => {
                const categoryId = categoryIdLookup[s.subject];
                if (!categoryId) {
                    console.error(`Could not find categoryId for subject: ${s.subject}`);
                }
                return {
                    categoryId: categoryId,
                    weighage: Number(s.weight),
                    difficultyLevel: s.difficulty,
                    createdUserId: "ADMIN" // Or use value from useAuth()
                };
            })
        };
        if (payload.adminTemplateCategoryMap.some(item => !item.categoryId)) {
            setToast('Error: An invalid category was selected. Please try again.');
            return;
        }

        try {
            await fetchAPI('/api/adminTemplate/create', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload),
            });
            setToast('Template created successfully! 🎉');
            const updatedTemplates = await fetchAPI('/api/adminTemplate/getAllAdminTemplates');
            setSavedTemplates(updatedTemplates);
            handleTemplateFormModalClose();
        } catch (err) {
            setToast(`Error: Could not save template. ${err.message}`);
        }
    };

    const handleUpdateTemplate = async () => {
        try {
            await fetchAPI(`/api/templates/update/${templateForm.id}`, { // mock ENDPOINT
                method: 'PATCH', // Or PUT
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(templateForm),
            });
            setToast('Template updated successfully!');
            const updatedTemplates = await fetchAPI('/api/adminTemplate/getAllAdminTemplates');
            setSavedTemplates(updatedTemplates);
            handleTemplateFormModalClose();
        } catch (err) {
            setToast(`Error: Could not update template. ${err.message}`);
        }
    };

    const deleteSavedTemplate = async (templateId) => {
        if (!window.confirm('Are you sure you want to delete this template?')) return;
        try {
            await fetchAPI(`/api/templates/delete/${templateId}`, { method: 'DELETE' }); // mock ENDPOINT
            setSavedTemplates(prev => prev.filter(t => t.id !== templateId));
            setToast('Template deleted successfully.');
        } catch (err) {
            setToast(`Error: Could not delete template. ${err.message}`);
        }
        handleTemplateMenuClose();
    };

    const handleTemplateMenuOpen = (event, template) => { setTemplateAnchorEl(event.currentTarget); setSelectedTemplate(template); };
    const handleTemplateMenuClose = () => setTemplateAnchorEl(null);
    const handleTemplateFormModalClose = () => { setTemplateFormModalOpen(false); setTemplateForm(initialTemplateFormState); setSelectedTemplate(null); };
    const handleTemplateViewModalClose = () => { setTemplateViewModalOpen(false); setSelectedTemplate(null); };
    const openCreateTemplateModal = () => { setTemplateModalMode('create'); setTemplateForm(initialTemplateFormState); setTemplateFormModalOpen(true); };
    const openViewTemplateModal = () => { setTemplateViewModalOpen(true); handleTemplateMenuClose(); };

    const openEditTemplateModal = () => {
        setTemplateModalMode('edit');
        // This logic needs to be corrected to map API data back to form state
        // The original `setTemplateForm(selectedTemplate)` was likely incorrect
        // This is a simple placeholder:
        const formData = {
            id: selectedTemplate.adminTemplateId,
            name: selectedTemplate.adminTemplateName,
            description: selectedTemplate.description,
            subjects: selectedTemplate.adminTemplateCategoryMap.map(s => ({
                id: s.category.categoryId, // Using categoryId as a unique key
                subject: s.category.categoryName,
                weight: s.weighage,
                difficulty: s.difficultyLevel
            }))
        };
        setTemplateForm(formData);
        setTemplateViewModalOpen(false);
        setTemplateFormModalOpen(true);
        handleTemplateMenuClose();
    };

    return (
        <>
            <Paper sx={{ p: 3 }}>
                <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 3 }}>
                    <Typography variant="h6">Saved Templates</Typography>
                    <Button variant="contained" startIcon={<AddCircleOutlineIcon />} onClick={openCreateTemplateModal}>
                        Create Template
                    </Button>
                </Stack>
                {isLoadingTemplates ? <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}><CircularProgress /></Box> :
                    templateError ? <Alert severity="error">{templateError}</Alert> :
                        <TableContainer>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>Template Name</TableCell>
                                        <TableCell>Description</TableCell>
                                        <TableCell>Category & Weights</TableCell>
                                        <TableCell>Created Date</TableCell>
                                        <TableCell align="right">Actions</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {savedTemplates.length === 0 ? <TableRow><TableCell colSpan={5} align="center">No templates found.</TableCell></TableRow> :
                                        savedTemplates.map(template => (
                                            <TableRow key={template.adminTemplateId} hover>
                                                <TableCell sx={{ fontWeight: 'medium' }}>{template.adminTemplateName}</TableCell>
                                                <TableCell title={template.description} sx={{ maxWidth: 200, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}> {template.description || 'N/A'}</TableCell>
                                                <TableCell>
                                                    <Stack direction="column" spacing={0.5} alignItems="flex-start">
                                                        {template.adminTemplateCategoryMap.map(sub => (
                                                            <Chip
                                                                key={sub.category.categoryId}
                                                                label={`${sub.category.categoryName}: ${sub.weighage}% (${sub.difficultyLevel})`}
                                                                size="small"
                                                            />
                                                        ))}
                                                    </Stack>
                                                </TableCell>
                                                <TableCell>{template.createdDate ? new Date(template.createdDate).toLocaleDateString() : 'N/A'}</TableCell>
                                                <TableCell align="right">
                                                    <IconButton onClick={(e) => handleTemplateMenuOpen(e, template)}><MoreVertIcon /></IconButton>
                                                </TableCell>
                                            </TableRow>
                                        ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                }
            </Paper>

            {/* --- TEMPLATE ACTION MENU --- */}
            <Menu anchorEl={templateAnchorEl} open={Boolean(templateAnchorEl)} onClose={handleTemplateMenuClose}>
                <MenuItem onClick={openViewTemplateModal}>View</MenuItem>
                <MenuItem onClick={openEditTemplateModal}>Edit</MenuItem>
                <MenuItem onClick={() => deleteSavedTemplate(selectedTemplate.adminTemplateId)} sx={{ color: 'error.main' }}>Delete</MenuItem>
            </Menu>

            {/* --- CREATE/EDIT TEMPLATE DIALOG --- */}
            <Dialog open={isTemplateFormModalOpen} onClose={handleTemplateFormModalClose} maxWidth="md" fullWidth>
                <DialogTitle>{templateModalMode === 'create' ? 'Create New Template' : 'Edit Template'}</DialogTitle>
                <DialogContent>
                    <Stack spacing={3} sx={{ mt: 2 }}>
                        <TextField label="Template Name" placeholder="e.g., React Full stack Assessment" value={templateForm.name} onChange={(e) => setTemplateForm({ ...templateForm, name: e.target.value })} />
                        <TextField label="Description" placeholder="Add a brief description for this template" rows={3}
                            value={templateForm.description || ''}
                            onChange={(e) => setTemplateForm({ ...templateForm, description: e.target.value })}
                        />
                        <Typography variant="subtitle2" color="text.secondary">Category</Typography>
                        {templateForm.subjects.map((s, index) => (
                            <Stack direction="row" spacing={2} key={s.id} alignItems="center">
                                <TextField select label={`Category ${index + 1}`} value={s.subject} onChange={e => handleSubjectChange(s.id, 'subject', e.target.value)} sx={{ flex: 2 }}>
                                    {availableSubjects.map(sub => <MenuItem key={sub.categoryName} value={sub.categoryName}>{sub.categoryName}</MenuItem>)}
                                </TextField>
                                <TextField label="Weight (%)" placeholder="e.g., 50" type="number" value={s.weight} onChange={e => handleSubjectChange(s.id, 'weight', e.target.value)} sx={{ flex: 1 }} />
                                <TextField select label="Difficulty" value={s.difficulty} onChange={e => handleSubjectChange(s.id, 'difficulty', e.target.value)} sx={{ flex: 1.5 }}>
                                    {availableDifficulties.map(diff => <MenuItem key={diff} value={diff}>{diff}</MenuItem>)}
                                </TextField>
                                {templateForm.subjects.length > 1 && <IconButton onClick={() => removeSubjectRow(s.id)} color="error"><DeleteIcon /></IconButton>}
                            </Stack>
                        ))}
                        <Box>
                            <Button startIcon={<AddCircleOutlineIcon />} onClick={addSubjectRow}>Add Category</Button>
                        </Box>
                        <Typography variant="h6" alignSelf="flex-end">Total Weight: <Box component="span" sx={{ color: totalWeight === 100 ? 'success.main' : 'error.main', ml: 1, fontWeight: 'bold' }}>{totalWeight}%</Box></Typography>
                    </Stack>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleTemplateFormModalClose}>Cancel</Button>
                    <Button variant="contained" onClick={handleTemplateFormSubmit} disabled={!canCreateTemplate}>
                        {templateModalMode === 'create' ? 'Create Template' : 'Save Changes'}
                    </Button>
                </DialogActions>
            </Dialog>

            {/* --- VIEW TEMPLATE DIALOG --- */}
            {selectedTemplate && (
                <Dialog open={isTemplateViewModalOpen} onClose={handleTemplateViewModalClose} maxWidth="sm" fullWidth>
                    <DialogTitle>Template Details</DialogTitle>
                    <DialogContent>
                        <List disablePadding>
                            <ListItem><ListItemText primary="Template Name" secondary={selectedTemplate.adminTemplateName} /></ListItem>
                            <Divider component="li" />
                            <ListItem><ListItemText primary="Description" secondary={selectedTemplate.description || 'N/A'} /></ListItem>
                            <Divider component="li" />
                            <ListItem>
                                <ListItemText
                                    primary="Category"
                                    secondary={
                                        <Stack direction="column" spacing={0.5} alignItems="flex-start" sx={{ mt: 1 }}>
                                            {selectedTemplate.adminTemplateCategoryMap.map(sub => (
                                                <Chip
                                                    key={sub.category.categoryId}
                                                    label={`${sub.category.categoryName}: ${sub.weighage}% (${sub.difficultyLevel})`}
                                                    size="small"
                                                />
                                            ))}
                                        </Stack>
                                    }
                                />
                            </ListItem>
                        </List>
                    </DialogContent>
                    <DialogActions sx={{ justifyContent: 'space-between', px: 3, pb: 2 }}>
                        <Button onClick={handleTemplateViewModalClose}>Close</Button>
                        <Button variant="contained" startIcon={<EditIcon />} onClick={openEditTemplateModal}>Edit Template</Button>
                    </DialogActions>
                </Dialog>
            )}
        </>
    );
}