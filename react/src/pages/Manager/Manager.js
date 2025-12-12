import { useState, useEffect } from 'react';
import {  Box, Container, Paper, Tabs, Tab} from '@mui/material';
import { RadialLinearScale, Chart, ArcElement, BarElement, 
  CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend } from 'chart.js';
import DashboardTab from '../Manager/dashboard';
import ResultsTab from '../Manager/ResultsTab';
import ReviewQuestionnaire from './ReviewQuestionnaire';
import AssessmentHubTab from '../Manager/AssessmentHub';
import { useAuth } from '../../AuthContext';


Chart.register(ArcElement, RadialLinearScale, BarElement, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend);

export default function Manager() {
  const { currentUser } = useAuth();
  const [tab, setTab] = useState(0);
  const [toast, setToast] = useState('');

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Paper sx={{ p: 3 }}>
        <Tabs value={tab} onChange={(_, v) => setTab(v)} aria-label="Manager tabs">
          <Tab label="Dashboard" />
          <Tab label="Review Questionnaire" />
          <Tab label="Assessment Hub" />
          {/* <Tab label="Results" /> */}
        </Tabs>
      </Paper>
      <Box>
        {tab === 0 && <DashboardTab />}
        {tab === 1 && <ReviewQuestionnaire setToast={setToast} />}
        {tab === 2 && <AssessmentHubTab />}
        {/* {tab === 2 && <ResultsTab />} */}
      </Box>
    </Container>
  );
}