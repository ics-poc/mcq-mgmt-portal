import {
  Paper, Typography, Button, Chip, Table, TableBody,
  TableCell, TableContainer, TableHead, TableRow
} from '@mui/material';

export default function ExamList({ exams, onAttend, onViewResult }) {
  return (
    <Paper sx={{ p: 3, borderRadius: 2 }}>
      <Typography variant="h5" gutterBottom>My Assessments</Typography>
      <TableContainer>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell sx={{ fontWeight: 'bold' }}>Assessment Name</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Skill Level</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Schedule Date</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Status</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }} align="center">Action</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {exams.map((exam) => (
              <TableRow key={exam.managerTemplateId}>
                <TableCell>{exam.managerTemplateName}</TableCell>
                <TableCell>{exam.difficultyLevel}</TableCell>
                <TableCell>{exam.assignedDate}</TableCell>
                <TableCell><Chip label={exam.status} color={exam.status === 'Completed' ? 'success' : 'warning'} size="small" /></TableCell>
                <TableCell align="center">
                  {exam.status === 'Completed' ? (
                    <Button variant="outlined" onClick={() => onViewResult(exam.managerTemplateId)}>View Result</Button>
                  ) : (
                    <Button variant="contained" onClick={() => onAttend(exam)}>Attend Exam</Button>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Paper>
  );
}