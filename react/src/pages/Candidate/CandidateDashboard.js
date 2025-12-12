import { useState, useEffect } from 'react';
import {
  Box, Container, Button, Dialog, DialogTitle,
  DialogContent, DialogActions, CircularProgress
} from '@mui/material';
import { useAuth } from '../../AuthContext';
import { api } from '../../api'; 
import ExamList from './ExamList'; 
import ExamPage from './ExamPage';
import ResultAnalysis from './ResultAnalysis';

export default function CandidateDashboard() {
  const { currentUser } = useAuth();
  const [exams, setExams] = useState([]);
  const [loadingExams, setLoadingExams] = useState(true);
  const [currentView, setCurrentView] = useState('home');
  const [selectedExam, setSelectedExam] = useState(null);
  const [resultInfo, setResultInfo] = useState(null);
  const [infoOpen, setInfoOpen] = useState(false);
  const CANDIDATE_ID = currentUser?.id;

  useEffect(() => {
    const loadUserExams = async () => {
      setLoadingExams(true);
      try {
        const allExamsList = await api.getAllExams(CANDIDATE_ID);
        setExams(allExamsList);
      } catch (e) {
        console.error("Failed to load exams:", e);
        setExams([]); 
      }
      setLoadingExams(false);
    };
    
    if (currentView === 'home' && CANDIDATE_ID) {
      loadUserExams();
    }
  }, [currentView, CANDIDATE_ID]); 

  const handleAttendExam = (exam) => {
    setSelectedExam(exam);
    setCurrentView('exam');
  };

  const handleViewResult = async (examId) => {
    setResultInfo(null); 
    setInfoOpen(true); 
    try {
      const resultData = await api.getResults(CANDIDATE_ID, examId);
      setResultInfo(resultData); 
    } catch (e) {
      console.error('Failed to fetch result:', e);
      setResultInfo(null); 
    }
  };

  const handleExamComplete = () => {
    setCurrentView('home'); 
    setSelectedExam(null);
  };

  if (loadingExams && currentView === 'home') {
    return (
      <Container maxWidth="lg" sx={{ py: 4, textAlign: 'center' }}>
        <CircularProgress />
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      {currentView === 'home' ? (
        <ExamList 
          exams={exams} 
          onAttend={handleAttendExam} 
          onViewResult={handleViewResult} 
        />
      ) : (
        <ExamPage 
          examDetails={selectedExam} 
          onComplete={handleExamComplete} 
        />
      )}

      <Dialog open={infoOpen} onClose={() => setInfoOpen(false)} fullWidth maxWidth="md">
        <DialogTitle sx={{ borderBottom: '1px solid', borderColor: 'divider' }}>
          Assessment Result & Analysis
        </DialogTitle>
        <DialogContent dividers>
          {resultInfo ? (
            <ResultAnalysis result={resultInfo} />
          ) : (
            <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
              <CircularProgress />
          _ </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button variant="contained" onClick={() => setInfoOpen(false)}>Close</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}