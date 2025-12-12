import { useEffect, useState } from 'react';
import {
    Paper, Button, Stack, Typography, TableContainer, Table, TableHead, TableRow,
    TableCell, TableBody, IconButton, MenuItem, Chip, Box, CircularProgress, Dialog, DialogTitle,
    DialogContent, DialogActions, Menu, List, ListItem, ListItemText, TablePagination,
    ButtonGroup, FormControl, FormLabel
} from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import { adminApi,fetchAPI } from '../../api'; 

export default function ReviewQuestionnaire({ setToast }) {
    const [reviewUploads, setReviewUploads] = useState([]);
    const [isLoadingReviews, setIsLoadingReviews] = useState(false);
    const [reviewAnchorEl, setReviewAnchorEl] = useState(null);
    const [selectedReviewItem, setSelectedReviewItem] = useState(null);
    const [isReviewModalOpen, setReviewModalOpen] = useState(false);
    const [reviewMode, setReviewMode] = useState('review');
    const [reviewQuestions, setReviewQuestions] = useState([]);
    const [allFetchedQuestions, setAllFetchedQuestions] = useState([]);
    const [isLoadingQuestions, setIsLoadingQuestions] = useState(false);
    const [isSubmittingReview, setIsSubmittingReview] = useState(false);
    const [questionSelections, setQuestionSelections] = useState({});
    const [reviewPagination, setReviewPagination] = useState({ page: 0, rowsPerPage: 5, totalElements: 0 });

    useEffect(() => {
        const fetchReviewData = async () => {
            setIsLoadingReviews(true);
            try {
                const data = await fetchAPI('/api/categories/getAllCategories');
                setReviewUploads(Array.isArray(data) ? data : []); 
            } catch (err) {
                setToast(`Failed to load reviewable questionnaires: ${err.message}`);
                setReviewUploads([]);
            } finally {
                setIsLoadingReviews(false);
            }
        };
        fetchReviewData();
    }, [setToast]);

    const handleReviewMenuOpen = (event, item) => {
        setReviewAnchorEl(event.currentTarget);
        setSelectedReviewItem(item);
    };
    const handleReviewMenuClose = () => {
        setReviewAnchorEl(null);
    };

    const fetchQuestions = async (categoryId, mode) => {
        setIsLoadingQuestions(true);
        setReviewQuestions([]);
        setAllFetchedQuestions([]);
        try {
            const data = await adminApi.fetchQuestionsForReview(categoryId, 0, 1000, mode);
            if (Array.isArray(data)) {
                const mappedQuestions = data.map(item => ({
                    id: item.questionId,
                    text: item.question,
                    options: item.options.map(optText => ({
                        text: optText,
                        correct: optText === item.answer
                    })),
                    status: item.statusFlag
                }));
                setAllFetchedQuestions(mappedQuestions);
                setReviewPagination(prev => ({
                    ...prev,
                    totalElements: mappedQuestions.length,
                    page: 0
                }));
                setReviewQuestions(mappedQuestions.slice(0, reviewPagination.rowsPerPage));
            if (mode === 'resume') {
                    const existingSelections = mappedQuestions.reduce((acc, q) => {
                        if (q.status === 'REJECTED' || q.status === 'R') {
                            acc[q.id] = 'R';
                        } else if (q.status === 'APPROVED' || q.status === 'A') {
                             acc[q.id] = 'A';
                    }
                    return acc;
                }, {});
                setQuestionSelections(existingSelections);
            } else {
                setQuestionSelections({});
                }
            } else {
                 throw new Error("Unexpected API response format: expected an array.");
            }
        } catch (err) {
            setToast(`Failed to fetch questions: ${err.message}`);
        } finally {
            setIsLoadingQuestions(false);
        }
    };

    const openReviewModal = (mode) => {
        if (!selectedReviewItem) return;
        setReviewMode(mode);
        setReviewModalOpen(true);
        fetchQuestions(selectedReviewItem.categoryId, mode);
        handleReviewMenuClose();
    };

    const handleReviewModalClose = () => {
        setReviewModalOpen(false);
        setSelectedReviewItem(null);
        setReviewQuestions([]);
        setAllFetchedQuestions([]); // Clear the full list
        setQuestionSelections({});
        setReviewPagination({ page: 0, rowsPerPage: 5, totalElements: 0 });
    };

    const handleQuestionSelection = (questionId, status) => {
        setQuestionSelections(prev => {
            if (prev[questionId] === status) {
                const newState = { ...prev };
                delete newState[questionId];
                return newState;
            }
            return { ...prev, [questionId]: status };
        });
    };

    const handleReviewPageChange = (event, newPage) => {
        const startIndex = newPage * reviewPagination.rowsPerPage;
        const endIndex = startIndex + reviewPagination.rowsPerPage;
        setReviewQuestions(allFetchedQuestions.slice(startIndex, endIndex));
        setReviewPagination(prev => ({ ...prev, page: newPage }));
    };

    const handleReviewRowsPerPageChange = (event) => {
        const newRowsPerPage = parseInt(event.target.value, 10);
        setReviewQuestions(allFetchedQuestions.slice(0, newRowsPerPage));
        setReviewPagination(prev => ({ ...prev, page: 0, rowsPerPage: newRowsPerPage }));
    };

    const handleSubmitReview = async () => {
        if (Object.keys(questionSelections).length === 0) {
            setToast('No decisions made. Please approve or reject at least one question.');
            return;
        }
        setIsSubmittingReview(true);
        const payload = {
            questions: questionSelections
        };
        try {
            const response = await adminApi.submitReview(selectedReviewItem.categoryId, payload);
            setReviewUploads(prev => prev.map(item =>
                item.categoryId === selectedReviewItem.categoryId
                    ? { ...item, status: response.newStatus || 'Review Completed' }
                    : item
            ));
            setToast('Review submitted successfully!');
            handleReviewModalClose();
        } catch (err) {
            setToast(`Failed to submit review: ${err.message}`);
        } finally {
            setIsSubmittingReview(false);
        }
    };

    const getStatusChipColor = (status) => {
        switch (status) {
            case 'Review Scheduled': return 'primary';
            case 'Review In Progress': return 'warning';
            case 'Review Completed': return 'success';
            default: return 'default';
        }
    };

    return (
        <>
            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 3 }}>Review Questionnaires</Typography>
                <TableContainer>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Category ID</TableCell>
                                <TableCell>Category Name</TableCell>
                                <TableCell>Sub-category</TableCell>
                                <TableCell>Upload Date</TableCell>
                                <TableCell>Status</TableCell>
                                <TableCell align="right">Actions</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {isLoadingReviews ? (
                                <TableRow><TableCell colSpan={6} align="center"><CircularProgress /></TableCell></TableRow>
                            ) : reviewUploads.length === 0 ? (
                                <TableRow><TableCell colSpan={6} align="center">No questionnaires to review.</TableCell></TableRow>
                            ) : (
                                reviewUploads.map((item) => (
                                    <TableRow key={item.categoryId} hover>
                                        <TableCell>{item.categoryId}</TableCell>
                                        <TableCell>{item.categoryName}</TableCell>
                                        <TableCell>{item.subCategoryName || 'N/A'}</TableCell>
                                        <TableCell>{new Date(item.createdDate).toLocaleString()}</TableCell>
                                        <TableCell>
                                            <Chip
                                                label={item.status}
                                                color={getStatusChipColor(item.status)}
                                                size="small"
                                            />
                                        </TableCell>
                                        <TableCell align="right">
                                            <IconButton onClick={(e) => handleReviewMenuOpen(e, item)}>
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
            <Menu anchorEl={reviewAnchorEl} open={Boolean(reviewAnchorEl)} onClose={handleReviewMenuClose}>
                {selectedReviewItem?.status === 'Review Scheduled' && (
                    <MenuItem onClick={() => openReviewModal('review')}>Review</MenuItem>
                )}
                {selectedReviewItem?.status === 'Review In Progress' && (
                    <MenuItem onClick={() => openReviewModal('resume')}>Resume Review</MenuItem>
                )}
                {selectedReviewItem?.status === 'Review Completed' && (
                    <MenuItem onClick={() => openReviewModal('view')}>View Review</MenuItem>
                )}
            </Menu>

            {selectedReviewItem && (
                <Dialog open={isReviewModalOpen} onClose={handleReviewModalClose} maxWidth="md" fullWidth>
                    <DialogTitle>
                        {reviewMode === 'review' && 'Review Questions'}
                        {reviewMode === 'resume' && 'Resume Review'}
                        {reviewMode === 'view' && 'View Selected Questions'}
                        <Typography variant="body2" color="text.secondary">{selectedReviewItem.categoryName} - {selectedReviewItem.subCategoryName}</Typography>
                    </DialogTitle>
                    <DialogContent>
                        {isLoadingQuestions ? (
                            <Box sx={{ display: 'flex', justifyContent: 'center', my: 5 }}><CircularProgress /></Box>
                        ) : reviewQuestions.length === 0 ? (
                            <Typography align="center" sx={{ my: 5 }}>No questions to display.</Typography>
                        ) : (
                            <Stack spacing={3} sx={{ mt: 2 }}>
                                {reviewQuestions.map((q, qIndex) => (
                                    <Paper key={q.id} variant="outlined" sx={{ p: 2 }}>
                                        <Typography variant="subtitle1" fontWeight="bold" sx={{ mb: 1 }}>
                                            Question {reviewPagination.page * reviewPagination.rowsPerPage + qIndex + 1}
                                        </Typography>
                                        <Typography variant="body1" sx={{ mb: 2 }}>{q.text}</Typography>
                                        <FormControl component="fieldset" fullWidth>
                                            <FormLabel component="legend">Options</FormLabel>
                                            <List disablePadding>
                                                {q.options.map((opt, oIndex) => (
                                                    <ListItem key={oIndex} disablePadding sx={{
                                                        color: opt.correct ? 'success.dark' : 'text.primary',
                                                        fontWeight: opt.correct ? 'bold' : 'normal'
                                                    }}>
                                                        <ListItemText
                                                            primary={`${String.fromCharCode(65 + oIndex)}. ${opt.text}`}
                                                            primaryTypographyProps={{
                                                                sx: {
                                                                    fontWeight: opt.correct ? 'bold' : 'normal',
                                                                    color: opt.correct ? 'success.dark' : 'inherit'
                                                                }
                                                            }}
                                                        />
                                                    </ListItem>
                                                ))}
                                            </List>
                                        </FormControl>
                                        {reviewMode !== 'view' && (
                                            <Box sx={{ mt: 2, display: 'flex', justifyContent: 'flex-end' }}>
                                                <ButtonGroup size="small">
                                                    <Button
                                                        variant={questionSelections[q.id] === 'A' ? 'contained' : 'outlined'}
                                                        color="success"
                                                        onClick={() => handleQuestionSelection(q.id, 'A')}>
                                                        Approve
                                                    </Button>
                                                    <Button
                                                        variant={questionSelections[q.id] === 'R' ? 'contained' : 'outlined'}
                                                        color="error"
                                                        onClick={() => handleQuestionSelection(q.id, 'R')}>
                                                        Reject
                                                    </Button>
                                                </ButtonGroup>
                                            </Box>
                                        )}
                                    </Paper>
                                ))}
                            </Stack>
                        )}
                    </DialogContent>
                    <DialogActions sx={{ justifyContent: 'space-between', p: 2, flexWrap: 'wrap' }}>
                        <Box sx={{ minWidth: 150, mb: { xs: 1, sm: 0 } }}>
                            {reviewMode !== 'view' && (
                                <Button
                                    variant="contained"
                                    onClick={handleSubmitReview}
                                    disabled={isSubmittingReview}
                                >
                                    {isSubmittingReview ? <CircularProgress size={24} /> : 'Submit Review'}
                                </Button>
                            )}  </Box>
                        <TablePagination
                            component="div"
                            count={reviewPagination.totalElements}
                            page={reviewPagination.page}
                            onPageChange={handleReviewPageChange}
                            rowsPerPage={reviewPagination.rowsPerPage}
                            onRowsPerPageChange={handleReviewRowsPerPageChange}
                            rowsPerPageOptions={[5, 10, 25]}
                            sx={{ minWidth: 300, flexShrink: 0 }}
                        />
                        <Button onClick={handleReviewModalClose} sx={{ minWidth: 80, mt: { xs: 1, sm: 0 } }}>Close</Button>
                    </DialogActions>
                </Dialog>
            )}
        </>
    );
}