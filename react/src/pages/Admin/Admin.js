import { useState } from 'react';
import {  Container, Paper, Tabs, Tab, Snackbar, Alert} from '@mui/material';
import { useAuth } from '../../AuthContext';
import ManageUsers from './ManageUsers';
import QuestionnaireUpload from './QuestionnaireUpload';
import ReviewQuestionnaire from '../Manager/ReviewQuestionnaire';
import AssessmentTemplate from './AssessmentTemplate';

export default function Admin() {
  const { currentUser } = useAuth(); 
  const [tab, setTab] = useState(0);
  const [toast, setToast] = useState('');

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Paper sx={{ p: 3, mb: 2 }}>
        <Tabs value={tab} onChange={(_, v) => setTab(v)} aria-label="Admin tabs">
          <Tab label="Manage Users" />
          <Tab label="Questionnaire Upload" />
          <Tab label="Review Questionnaire" />
          <Tab label="Assessment Template" />
        </Tabs>
      </Paper>

      <Snackbar open={!!toast} autoHideDuration={3000} onClose={() => setToast('')} anchorOrigin={{ vertical: 'top', horizontal: 'center' }}>
        <Alert severity="info" onClose={() => setToast('')} sx={{ width: '100%' }}>{toast}</Alert>
      </Snackbar>
      {tab === 0 && <ManageUsers setToast={setToast} />}
      {tab === 1 && <QuestionnaireUpload setToast={setToast} />}
      {tab === 2 && <ReviewQuestionnaire setToast={setToast} />}
      {tab === 3 && <AssessmentTemplate setToast={setToast} />}
    </Container>
  );
}